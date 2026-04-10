---
name: 핵심 도메인 구조 및 API 엔드포인트
description: IntervAI 면접 기능 도메인 패키지 구조, 엔티티, API 경로 정리
type: project
---

## 면접 관련 도메인 패키지

- `interview` — 면접 설정 (Interview, InterviewType, Difficulty, InterviewerTone, CsCategory, CsSubject)
- `session` — 면접 세션 (InterviewSession, InterviewSessionStatus: IN_PROGRESS/COMPLETED)
- `question` — 질문 (Question, QuestionType: QUESTION/FOLLOW_UP)
- `answer` — 답변 (Answer, AnswerResult, AnswerResultGenerator 인터페이스)
- `feedback` — 피드백 (Feedback)

## 주요 도메인 결정 사항

- `Interview`와 `InterviewSession`이 분리된 도메인. interview는 설정 정보, session은 진행 상태 관리.
- `InterviewSession`에 `currentMainQuestionIdx`와 `followUpCount` 필드로 현재 진행 위치 관리.
- `maxFollowUpCount`는 Interview에 고정값 3 (`DEFAULT_MAX_FOLLOW_UP_COUNT`).
- 꼬리 질문 여부는 `QuestionType.FOLLOW_UP`으로 구분.
- 답변 제출 후 꼬리 질문/다음 질문 분기는 `AnswerService.decideNextQuestion()`에서 처리.

## 구현된 API 엔드포인트 (면접 기능)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/interviews` | 면접 설정 생성 |
| POST | `/api/interviews/{id}/sessions` | 세션 생성 |
| POST | `/api/interviews/{id}/questions` | 질문 일괄 생성 (LLM) |
| GET | `/api/interviews/{id}/questions/current` | 현재 질문 조회 |
| POST | `/api/interviews/{id}/answers` | 답변 제출 + 피드백 |
| POST | `/api/interviews/{id}/finish` | 세션 종료 |

## LLM 인터페이스

- `QuestionGenerator` 인터페이스 → `OllamaQuestionGenerator`(실제), `MockQuestionGenerator`(테스트)
- `AnswerResultGenerator` 인터페이스 → `ClaudeAnswerResultGenerator`(실제), `MockAnswerResultGenerator`(테스트)
- 프롬프트 파일: `question-generator.st`, `feedback-followup.st`, `summary.st`

**Why:** 코드 탐색 없이 도메인 구조를 빠르게 파악하기 위해 기록.

**How to apply:** 새 기능 추가 또는 문서 작성 시 도메인 용어와 패키지 구조 참조.
