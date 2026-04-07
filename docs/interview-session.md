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
