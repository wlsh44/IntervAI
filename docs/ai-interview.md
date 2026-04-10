# AI 면접 기능

## 개요
- 목적: Claude API(LLM)를 활용하여 포트폴리오 기반 맞춤형 질문을 생성하고, 사용자 답변에 대한 피드백과 꼬리 질문을 제공하여 실질적인 면접 연습 경험을 제공한다.
- 대상 사용자: AI 면접 연습 중인 사용자
- 관련 개발 단계: Stage 3 (LLM 연동 기본 채팅), Stage 4 (꼬리 질문 + 피드백), Stage 6 (포트폴리오 기반 질문 고도화)

## 진행 상황

| 단계 | 상태 | 비고 |
|------|------|------|
| Stage 3 — LLM 연동 기본 채팅 | ✅ 완료 | |
| Stage 4 — 꼬리 질문 + 피드백 | 🔧 진행 중 | 피드백 구조화 미구현, conversationId 분리 미구현 |
| Stage 6 — 포트폴리오 기반 질문 고도화 | ⬜ 예정 | |

## 요구사항
### 기능 요구사항

#### 질문 생성
- [x] CS 기초 질문 생성: CsCategory(DATA_STRUCTURE, ALGORITHM, NETWORK, LANGUAGE, DATABASE) 및 세부 토픽 기반
- [x] 포트폴리오 기반 질문 생성: portfolioLinks를 시스템 프롬프트에 주입하여 맞춤 질문 생성
- [x] 종합(ALL) 유형: CS + 포트폴리오 기반 질문을 혼합하여 생성
- [x] 세션 생성 후 별도 API 호출로 질문 일괄 생성하여 DB에 저장 (`POST /api/interviews/{interviewId}/questions`)
- [ ] GitHub 레포지토리의 기술 스택, 커밋 패턴, README 분석을 통한 고도화된 포트폴리오 질문 (Stage 6)

#### 꼬리 질문 (Follow-up)
- [x] 사용자의 답변 내용을 분석하여 연계된 추가 질문 생성
- [x] 꼬리 질문 수 제한 준수 (maxFollowUpCount, 기본값 3 — `Interview.DEFAULT_MAX_FOLLOW_UP_COUNT`)
- [x] 꼬리 질문 초과 또는 LLM이 꼬리 질문 없음 판단 시 다음 본 질문으로 자동 진행
- [x] 꼬리 질문은 `QuestionType.FOLLOW_UP`으로 저장, 본 질문은 `QuestionType.QUESTION`으로 구분
- [ ] 본 질문 변경 시 꼬리 질문 컨텍스트 분리 (conversationId 재발급 — 미구현)

#### 답변 피드백
- [x] 답변 후 피드백 생성 (Claude API `feedback-followup.st` 프롬프트 사용)
- [x] 피드백은 답변 제출 응답(`CreateAnswerResponse`)에 포함하여 반환
- [ ] 피드백에 이상적인 모범 답변 예시 포함 (현재 단순 텍스트 피드백만 반환)

#### LLM 컨텍스트 관리
- [x] 시스템 프롬프트에 면접관 페르소나 설정 (FRIENDLY / NORMAL / AGGRESSIVE)
- [x] Spring AI ChatClient 기반 LLM 호출 (`AnswerResultGenerator` 인터페이스)
- [x] conversationId로 세션 단위 대화 히스토리 관리 (sessionId를 conversationId로 사용)
- [ ] 본 질문 변경 시 conversationId 재발급으로 꼬리 질문 컨텍스트 분리 (미구현)
- [ ] GitHub 분석 결과를 시스템 프롬프트에 주입 (Stage 6)

### 비기능 요구사항
- [x] LLM 프롬프트 파일은 외부 `.st` 파일로 분리 관리
  - `resources/prompts/question-generator.st` — 질문 생성
  - `resources/prompts/feedback-followup.st` — 피드백 + 꼬리 질문
  - `resources/prompts/summary.st` — 세션 요약 (Stage 6 예정)
- [x] `QuestionGenerator` / `AnswerResultGenerator` 인터페이스로 LLM 구현체 교체 가능하게 설계
  - Mock 구현체: `MockQuestionGenerator`, `MockAnswerResultGenerator`
  - 실제 구현체: `OllamaQuestionGenerator` (Claude API 연동), `ClaudeAnswerResultGenerator`

## 사용자 스토리
- As a 면접 연습 중인 사용자, I want to 내 포트폴리오에 기반한 맞춤형 질문을 받고 싶다 so that 실제 면접에서 받을 수 있는 질문을 미리 연습할 수 있다.
- As a 면접 연습 중인 사용자, I want to 내 답변에 연계된 꼬리 질문을 받고 싶다 so that 기술적 깊이를 더 깊이 탐구하는 연습을 할 수 있다.
- As a 면접 연습 중인 사용자, I want to 답변에 대한 구체적인 피드백을 받고 싶다 so that 내 약점을 파악하고 개선할 수 있다.
- As a 면접 연습 중인 사용자, I want to 피드백을 내가 원할 때 확인하고 싶다 so that 먼저 스스로 생각해보고 나서 비교할 수 있다.

## 사용자 플로우

### 질문 생성 플로우 (세션 생성 후)
1. 사용자가 면접 설정 완료 후 세션 생성 요청 (`POST /api/interviews/{interviewId}/sessions`)
2. 세션 생성 후 프론트엔드가 질문 생성 요청 (`POST /api/interviews/{interviewId}/questions`)
3. Backend가 `question-generator.st` 프롬프트로 Claude API에 질문 생성 요청
   - 변수: `{ interviewType, difficulty, questionCount, interviewerTone, csSubjects, portfolioLinks }`
4. Claude API가 질문 배열 반환
5. Backend가 질문을 `QuestionType.QUESTION`으로 DB에 저장하고 응답 반환

### 피드백 + 꼬리 질문 플로우 (답변 제출 시)
1. 사용자가 답변 제출 (`POST /api/interviews/{interviewId}/answers`)
   - body: `{ questionId, content }`
2. Backend가 `AnswerHandler`를 통해 `AnswerResultGenerator`에 피드백 + 꼬리 질문 요청
   - `feedback-followup.st` 프롬프트, sessionId를 conversationId로 사용
3. Claude API가 `{ feedback, followUpQuestion }` 반환
4. Backend가 답변 및 피드백을 DB에 저장
5. followUpQuestion 존재 여부 + maxFollowUpCount 비교로 다음 질문 결정:
   - 꼬리 질문 있음 + 한도 미달 → 꼬리 질문 저장 후 세션 followUpCount 증가
   - 꼬리 질문 없음 또는 한도 초과 → 세션 currentMainQuestionIdx 증가
6. 응답: `{ feedback }` — 프론트엔드는 피드백 숨김 처리 후 사용자 요청 시 표시

## 수용 기준 (Acceptance Criteria)
- [x] CS 기초 질문이 선택한 CsCategory 및 토픽에 맞게 생성됨
- [x] 포트폴리오 기반 질문이 portfolioLinks를 반영하여 생성됨
- [x] 꼬리 질문이 사용자 답변 내용을 참조하여 생성됨
- [x] 꼬리 질문은 maxFollowUpCount(기본 3) 초과 시 생성되지 않음
- [x] 면접관 페르소나(FRIENDLY / NORMAL / AGGRESSIVE)에 따라 응답 톤이 달라짐
- [x] 피드백이 답변 제출 응답에 포함됨
- [ ] 피드백에 핵심 키워드, 논리 구조, 깊이 평가 항목이 구조화되어 포함됨 (현재 단순 텍스트)
- [ ] 본 질문 변경 시 꼬리 질문 컨텍스트가 분리됨 (conversationId 재발급)

## 범위 외 (Out of Scope)
- LLM 모델 교체 인터페이스 (Claude 외 GPT 등)
- 질문 수동 편집 기능
- 사용자 정의 프롬프트 입력
- 실시간 음성 피드백
- 스트리밍(SSE) 응답

## 미결 사항 (Open Questions)
- 포트폴리오 기반 질문에서 GitHub 레포지토리 분석 깊이 결정 (README만 vs 커밋 메시지 포함)
- LLM 호출 실패 시 재시도 횟수 및 fallback 처리 방법
- 꼬리 질문 생성 여부를 LLM이 자율 판단할지 항상 생성할지 결정
- 본 질문 이동 시 conversationId 재발급 구현 방법 (Redis 기반 ChatMemory 전환 여부)

## 관련 도메인/엔티티
- `Interview` — interviewType, difficulty, questionCount, maxFollowUpCount, interviewerTone, csSubjects, portfolioLinks
- `Question` — id, interviewId, sessionId, content, questionType (QUESTION / FOLLOW_UP), questionIndex
- `Answer` — id, questionId, sessionId, interviewId, content
- `Feedback` — id, answerId, content
- `InterviewerTone`: FRIENDLY, NORMAL, AGGRESSIVE
- `QuestionType`: QUESTION, FOLLOW_UP
- `CsCategory`: DATA_STRUCTURE, ALGORITHM, NETWORK, LANGUAGE, DATABASE
- 프롬프트 파일:
  - `resources/prompts/question-generator.st` — 질문 생성
  - `resources/prompts/feedback-followup.st` — 피드백 + 꼬리 질문
  - `resources/prompts/summary.st` — 세션 요약 (예정)
- `QuestionGenerator` 인터페이스 — `OllamaQuestionGenerator`(실제), `MockQuestionGenerator`(테스트)
- `AnswerResultGenerator` 인터페이스 — `ClaudeAnswerResultGenerator`(실제), `MockAnswerResultGenerator`(테스트)
