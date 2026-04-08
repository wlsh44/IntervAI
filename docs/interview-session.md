# 면접 세션

## 개요
- 목적: 사용자가 AI와 채팅 형식으로 면접을 진행할 수 있도록 면접 설정 생성·세션 관리를 처리하고, 질문·답변·피드백의 전체 흐름을 처리한다.
- 대상 사용자: 면접 연습을 원하는 로그인된 사용자
- 관련 개발 단계: Stage 3 (LLM 연동 기본 채팅), Stage 4 (꼬리 질문 + 피드백), Stage 5 (세션 기록 저장)

## 요구사항
### 기능 요구사항
- [x] 면접 설정 생성 (`POST /api/interviews`)
  - [x] 면접 유형: CS / PORTFOLIO / ALL
  - [x] 난이도: ENTRY / JUNIOR / SENIOR
  - [x] 질문 수 설정 (5개 이상 10개 이하)
  - [x] 꼬리 질문 최대 수 설정 (고정값 3 — `Interview.DEFAULT_MAX_FOLLOW_UP_COUNT`)
  - [x] CS 과목 선택 (`CsCategory` + 세부 토픽 목록): CS 또는 ALL 유형 필수
  - [x] 포트폴리오 링크 입력: PORTFOLIO 또는 ALL 유형 필수
  - [x] 면접관 페르소나 설정 (FRIENDLY / NORMAL / AGGRESSIVE)
  - [ ] 프로필 기본값 자동 적용 (미구현 — 프론트엔드에서 직접 입력)
- [x] 세션 생성 (`POST /api/interviews/{interviewId}/sessions`)
  - [x] 면접 소유자 검증 (타인 면접 접근 시 `INTERVIEW_ACCESS_DENIED` 403)
  - [x] 세션 초기 상태: IN_PROGRESS, currentMainQuestionIdx=0, followUpCount=0
- [x] 질문 일괄 생성 (`POST /api/interviews/{interviewId}/questions`)
  - [x] LLM에 questionCount만큼 질문 일괄 생성 요청 후 DB 저장
- [x] 현재 질문 조회 (`GET /api/interviews/{interviewId}/questions/current`)
  - [x] currentMainQuestionIdx와 followUpCount를 기준으로 현재 노출할 질문 반환
  - [x] 응답: `{ questionId, question, questionType, hasNext }`
- [x] 답변 제출 (`POST /api/interviews/{interviewId}/answers`)
  - [x] 답변 저장 + LLM 피드백 + 꼬리 질문 생성
  - [x] 꼬리 질문 존재 + 한도 미달 → 꼬리 질문 저장 및 followUpCount 증가
  - [x] 꼬리 질문 없음 또는 한도 초과 → currentMainQuestionIdx 증가
  - [x] 응답: `{ feedback }` — 피드백은 프론트엔드에서 숨김 처리
- [x] 세션 종료 (`POST /api/interviews/{interviewId}/finish`)
  - [x] 면접 소유자 검증
  - [x] 세션 IN_PROGRESS 상태 검증 (COMPLETED인 경우 `SESSION_ALREADY_COMPLETED` 400)
  - [x] 세션 상태를 COMPLETED로 업데이트

### 비기능 요구사항
- [x] 질문 목록은 세션 생성 후 별도 API 호출로 전체 생성 후 DB 저장, 프론트에는 1개씩 노출
- [x] 면접 소유자 검증은 `InterviewSessionValidator.validateInterviewOwner()`로 통일
- [ ] 포트폴리오 데이터를 시스템 프롬프트에 주입 (portfolioLinks는 현재 URL 텍스트로 주입, GitHub 분석 고도화는 Stage 6)
- [ ] 본 질문 변경 시 conversationId 재발급으로 꼬리 질문 컨텍스트 분리 (미구현)

## 사용자 스토리
- As a 로그인된 사용자, I want to 면접 유형과 난이도를 선택하여 새 면접 세션을 시작하고 싶다 so that 내 상황에 맞는 맞춤형 면접을 받을 수 있다.
- As a 면접 진행 중인 사용자, I want to 질문에 답변을 제출하면 다음 질문이 채팅 형식으로 이어지길 원한다 so that 실제 면접처럼 자연스러운 대화 흐름을 경험할 수 있다.
- As a 면접 진행 중인 사용자, I want to 피드백을 내가 원할 때만 확인하고 싶다 so that 다음 질문에 집중하며 스스로 먼저 생각해볼 수 있다.
- As a 면접 진행 중인 사용자, I want to 내 답변에 연계된 꼬리 질문을 받고 싶다 so that 깊이 있는 기술 대화를 연습할 수 있다.

## 사용자 플로우
1. 사용자가 면접 설정 페이지에서 유형, 난이도, 질문 수, 직군(CS 과목) 등을 설정한다.
2. `POST /api/interviews` — 면접 설정 저장, interviewId 반환.
3. `POST /api/interviews/{interviewId}/sessions` — 세션 생성, sessionId 반환.
4. `POST /api/interviews/{interviewId}/questions` — LLM에 질문 일괄 생성 요청.
5. `GET /api/interviews/{interviewId}/questions/current` — 첫 번째 질문 조회.
6. 사용자가 답변을 입력하고 제출한다 (`POST /api/interviews/{interviewId}/answers`).
7. 피드백과 다음 질문 정보가 반환된다.
   - 피드백은 프론트엔드에서 숨김 처리, "피드백 보기" 클릭 시 노출
8. `GET /api/interviews/{interviewId}/questions/current` — 현재 질문(꼬리 질문 또는 다음 본 질문) 조회.
9. 모든 질문이 완료되면 사용자가 세션 종료를 요청한다.
10. `POST /api/interviews/{interviewId}/finish` — 세션 상태를 COMPLETED로 업데이트.

## 수용 기준 (Acceptance Criteria)
- [x] `POST /api/interviews` — 면접 설정 생성 성공, 201 반환
  - CS 또는 ALL 유형에 csSubjects 미입력 시 `CS_SUBJECT_REQUIRED` (400) 반환
  - PORTFOLIO 또는 ALL 유형에 portfolioLinks 미입력 시 `PORTFOLIO_LINK_REQUIRED` (400) 반환
  - questionCount 5 미만 또는 10 초과 시 `INVALID_QUESTION_COUNT` (400) 반환
- [x] `POST /api/interviews/{interviewId}/sessions` — 세션 생성 성공, 201 반환
  - 타인 면접 접근 시 `INTERVIEW_ACCESS_DENIED` (403) 반환
- [x] `POST /api/interviews/{interviewId}/questions` — 질문 일괄 생성 및 DB 저장 성공, 201 반환
- [x] `GET /api/interviews/{interviewId}/questions/current` — 현재 질문 반환 (questionId, question, questionType, hasNext)
- [x] `POST /api/interviews/{interviewId}/answers` — 답변 저장 및 피드백 반환 성공, 201 반환
  - 이미 답변한 질문에 재답변 시 `ANSWER_ALREADY_EXISTS` (409) 반환
- [x] `POST /api/interviews/{interviewId}/finish` — 세션 상태 COMPLETED 업데이트, 200 반환 (body 없음)
  - 세션이 IN_PROGRESS 상태인 경우에만 처리
  - 이미 COMPLETED인 경우 `SESSION_ALREADY_COMPLETED` (400) 반환
  - 면접이 존재하지 않으면 `INTERVIEW_NOT_FOUND` (404) 반환
  - 타인 면접에 접근한 경우 `INTERVIEW_ACCESS_DENIED` (403) 반환
- [x] 면접관 페르소나(톤)가 시스템 프롬프트에 적용됨

## 면접 종료 시나리오

### 1. 중간 면접 종료 (사용자 임의 종료)

**결론**: 중간 종료 시에도 기존 `POST /api/interviews/{interviewId}/finish` API를 그대로 사용한다. 별도 상태값(`ABANDONED` 등)은 추가하지 않는다.

**근거**:
- `InterviewSessionStatus`는 현재 `IN_PROGRESS / COMPLETED` 두 값으로 구성되며, 중간 종료를 구분할 실질적인 비즈니스 이유가 현 단계(Stage 4)에 없다.
- 종합 리포트(Stage 6) 기획이 확정되기 전에 상태를 늘리면 이후 설계 변경 비용이 커진다. 필요 시 Stage 6에서 `ABANDONED` 상태 추가를 재검토한다.
- `/finish` API는 이미 `IN_PROGRESS` 상태 검증을 포함하고 있어 중간 종료 요청을 처리하는 데 문제가 없다.

**프런트엔드 담당**:
- 면접 진행 화면에 "면접 종료" 버튼 제공
- 버튼 클릭 시 "종료하면 현재까지의 면접만 저장됩니다. 종료하시겠습니까?" 확인 다이얼로그 표시 (사실상 필수 — 오조작 방지)
- 확인 후 `POST /api/interviews/{interviewId}/finish` 호출
- 응답 200 수신 후 결과 페이지로 이동

**백엔드 담당**:
- 기존 `/finish` API 동일하게 처리 — `IN_PROGRESS` 상태인 경우에만 `COMPLETED`로 전환
- 이미 COMPLETED인 경우 `SESSION_ALREADY_COMPLETED` (400) 반환 (중복 호출 방어)

---

### 2. 질문이 모두 끝난 경우 — 세션 상태 관리 주체

**결론**: 프런트엔드가 `hasNext: false`를 감지하고 `POST /finish`를 호출하는 현재 구조를 유지한다. 서버의 자동 완료 처리는 도입하지 않는다.

**근거**:
- 서버 자동 완료 방식의 문제: 마지막 답변 제출 시점(`POST /answers`)과 사용자가 실제로 "면접을 마쳤다"고 인지하는 시점이 다를 수 있다. 사용자가 마지막 피드백을 확인하기 전에 세션이 자동으로 닫히면 예상치 못한 상태 전이가 발생한다.
- 현재 백엔드 구현(`InterviewSessionService.finish`)은 명시적 종료 요청을 전제로 설계되어 있으며, 답변 제출 흐름(`answers` 처리)과 세션 종료 책임을 분리하고 있다. 이를 유지하는 것이 관심사 분리 원칙에 부합한다.
- `hasNext: false`는 "다음 질문 없음"을 나타낼 뿐, 세션을 종료해야 한다는 의미를 직접 내포하지 않는다. 종료 시점의 판단은 UI 레이어(프런트엔드)에 두는 것이 더 명확하다.

**프런트엔드 담당**:
- `POST /answers` 응답 후 `GET /questions/current`를 호출하여 `hasNext: false`인 경우 "모든 질문이 완료되었습니다" 안내 UI 표시
- 사용자에게 최종 확인 후 `POST /finish` 호출 (자동 호출하지 않고 명시적 트리거 권장)
- `SESSION_ALREADY_COMPLETED` (400) 수신 시 이미 종료된 면접으로 처리하여 결과 페이지로 이동

**백엔드 담당**:
- 현재 구현 유지 — `/finish`는 명시적 호출 시에만 세션을 종료
- `ALL_QUESTIONS_ANSWERED` (400): 모든 질문 완료 후 `/questions/current` 재호출 시 반환 (프런트 상태 동기화 용도로 활용 가능)

---

### 3. 추가 종료 시나리오 및 미결 처리

#### 3-1. 브라우저 탭 닫기 / 새로고침 / 네트워크 단절

**현황**: 세션이 `IN_PROGRESS` 상태로 영구히 남는다. 현재 `InterviewSessionStatus`에 만료 처리 메커니즘이 없다.

**결론 (현 단계 처리 방식)**:
- Stage 4~5 범위에서는 방치된 `IN_PROGRESS` 세션을 별도로 정리하지 않는다.
- 사용자가 재접속했을 때 이전 IN_PROGRESS 세션 복구 또는 강제 종료 처리는 Stage 5 (세션 기록 저장 및 히스토리 UI) 기획 시 함께 결정한다.

**미결 사항으로 등록**: 세션 만료 정책 (TTL 기반 자동 만료 vs 사용자 재접속 시 강제 종료 vs 방치 허용)

#### 3-2. 세션 중복 종료 시도

**현황**: 이미 구현됨. `SESSION_ALREADY_COMPLETED` (400) 반환.

**프런트엔드 담당**: 해당 에러 수신 시 "이미 종료된 면접입니다" 안내 후 결과 페이지로 이동.

#### 3-3. 세션 없는 상태에서 finish 호출

**현황**: 이미 구현됨. `SESSION_NOT_FOUND` (404) 반환.

#### 3-4. 답변 제출 중 오류 발생 후 종료 시도

**결론**: 서버는 현재 상태 기준으로만 판단한다. 일부 질문에 답변이 없어도 `/finish` 호출 시 `IN_PROGRESS`이면 정상 종료 처리한다. 미답변 질문 포함 여부는 리포트 생성 시 별도 처리한다(Stage 6).

---

## 범위 외 (Out of Scope)
- 다중 사용자 동시 진행 (멀티플레이어 면접)
- 음성 입력/출력
- 시간 제한 면접 모드
- 세션 일시 정지 및 이어하기 (종합 리포트 생성 전 중단 복구)
- 스트리밍(SSE) 응답

## 미결 사항 (Open Questions)
- 질문 생성 실패 시 재시도 정책 및 사용자 안내 방법
- 꼬리 질문 최대 수를 면접 설정 시 사용자가 지정 가능하게 할지 (현재 고정값 3)
- 포트폴리오 기반 유형에서 GitHub 레포지토리 분석 실패 시 처리 방식
- LLM 응답 지연 시 타임아웃 처리 방법
- 프로필 기본값 자동 적용 구현 방법 (프론트엔드 vs 백엔드)
- 세션 만료 정책: 방치된 `IN_PROGRESS` 세션 처리 방식 (TTL 기반 자동 만료 / 재접속 시 강제 종료 / 방치 허용) — Stage 5 기획 시 결정

## 관련 도메인/엔티티
- `Interview` — id, userId, interviewType (CS / PORTFOLIO / ALL), difficulty, questionCount, maxFollowUpCount, interviewerTone, csSubjects[], portfolioLinks[]
- `InterviewSession` — id, interviewId, userId, sessionStatus (IN_PROGRESS / COMPLETED), currentMainQuestionIdx, followUpCount, completedAt
- `Question` — id, interviewId, sessionId, content, questionType (QUESTION / FOLLOW_UP), questionIndex
- `Answer` — id, questionId, sessionId, interviewId, content
- `Feedback` — id, answerId, content
- `CsSubject` — category (CsCategory), topics[]
- `CsCategory`: DATA_STRUCTURE, ALGORITHM, NETWORK, LANGUAGE, DATABASE
- `InterviewType`: CS, PORTFOLIO, ALL
- `Difficulty`: ENTRY, JUNIOR, SENIOR
- `InterviewerTone`: FRIENDLY, NORMAL, AGGRESSIVE
- `QuestionType`: QUESTION, FOLLOW_UP
- 프롬프트 파일: `question-generator.st`, `feedback-followup.st`
