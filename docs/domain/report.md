# 종합 리포트

## 개요
- 목적: 면접 세션 종료 후 LLM이 전체 대화를 분석하여 종합 점수, 질문별 핵심 키워드, 전체 코멘트를 담은 리포트를 제공한다.
- 대상 사용자: 면접 세션을 완료한 사용자
- 관련 개발 단계: Stage 6 (종합 리포트 + 포트폴리오 기반 질문 고도화)

## 진행 상황

| 단계 | 상태 | 비고 |
|------|------|------|
| Stage 6 — 종합 리포트 생성 | ✅ 완료 | 비동기 생성, 별도 조회 API 제공 |
| Stage 6 — 포트폴리오 기반 질문 고도화 | ⬜ 예정 | |

## 요구사항
### 기능 요구사항
- [x] 세션 종료 시 전체 질문·답변·피드백 히스토리를 기반으로 종합 평가 리포트 생성 (비동기)
- [x] 종합 점수(`totalScore`, 0~100) 산출
- [x] 전체 종합 코멘트(`overallComment`) 제공 (2~3문장)
- [x] 세션 내 각 본 질문별 질문·답변·피드백·점수·키워드 요약 제공
- [x] 각 본 질문에 딸린 꼬리 질문·답변·피드백 목록 포함
- [x] 종합 리포트 결과 DB 저장 (`InterviewReport` 엔티티)
- [ ] 강점(`strengths`) 목록 제공 (미구현)
- [ ] 개선 방향(`improvements`) 목록 제공 (미구현)

### 비기능 요구사항
- [x] 리포트 생성은 `summary.st` 프롬프트 파일 사용
  - 프롬프트 변수: `{ interviewType, difficulty, jobCategory }`
  - 대화 히스토리는 `ChatMemory`(conversation_id = sessionId)로 자동 주입
- [x] 리포트 생성은 세션 종료(`POST /api/interviews/{interviewId}/finish`) 시점에 `@Async`로 비동기 트리거
- [x] 리포트 조회는 별도 API(`GET /api/interviews/{interviewId}/report`)로 제공
- [x] 세션 상태가 COMPLETED 이어야 리포트 조회 가능

## 사용자 스토리
- As a 면접을 완료한 사용자, I want to 내 전체 면접 성과를 종합적으로 평가받고 싶다 so that 내 강점과 약점을 객관적으로 파악할 수 있다.
- As a 면접을 완료한 사용자, I want to 각 질문별 내 답변과 피드백을 한눈에 볼 수 있고 싶다 so that 어떤 부분이 부족했는지 상세하게 복기할 수 있다.

## 사용자 플로우
1. 사용자가 모든 질문에 답변을 마치고 "세션 종료" 버튼을 누른다.
2. `POST /api/interviews/{interviewId}/finish` 호출 → 세션 상태 COMPLETED 업데이트 후 즉시 200 반환.
3. Backend가 `@Async`로 `InterviewReportService.requestGeneration()` 실행:
   - Interview / Session 컨텍스트 조회
   - `ReportGenerator.generate(sessionId, interview, jobCategory)` 호출
   - `summary.st` 프롬프트 + ChatMemory 히스토리로 LLM 요청
   - LLM 응답 파싱 → `ReportQuestionAssembler.assemble()`로 질문별 피드백·키워드 병합
   - `InterviewReportManager.create()`로 DB 저장
4. 사용자가 리포트 페이지에서 `GET /api/interviews/{interviewId}/report` 호출.
5. 리포트 페이지에서 종합 점수, 전체 코멘트, 질문별 답변·피드백·키워드·꼬리질문을 확인.

## 수용 기준 (Acceptance Criteria)
- [x] `POST /api/interviews/{interviewId}/finish` → 세션 COMPLETED 처리 후 200 반환, 리포트 비동기 생성 트리거
- [x] `GET /api/interviews/{interviewId}/report` → 리포트 응답 반환
- [x] 응답에 `totalScore`, `overallComment` 포함
- [x] 응답에 `questions[]` 포함 — 각 항목: `{ questionId, questionIndex, questionContent, answerContent, feedbackContent, score, keywords[], followUps[] }`
- [x] `followUps[]` 각 항목: `{ questionId, questionContent, answerContent, feedbackContent }`
- [x] 세션 상태가 COMPLETED로 업데이트됨
- [ ] 응답에 `strengths[]`, `improvements[]` 포함 (미구현)

## 범위 외 (Out of Scope)
- 여러 세션 간 성장 추이 그래프/비교 분석
- 리포트 공유 기능
- PDF 내보내기
- 특정 약점 기반 자동 재시험 추천

## 미결 사항 (Open Questions)
- 리포트 생성 실패(LLM 오류) 시 재시도 전략 (현재 예외 로깅만 처리)
- `strengths`, `improvements` 추가 여부 및 LLM 프롬프트 확장 범위
- 리포트 생성 완료 여부를 클라이언트에 알리는 방법 (polling vs SSE vs push)
- 점수 항목 세분화 여부 (현재 질문별 score + 종합 totalScore)

## 관련 도메인/엔티티
- `InterviewReport` — id, interviewId, interviewType, jobCategory, difficulty, questionCount, completedAt, totalScore, overallComment, `questions(List<ReportQuestion>)`
- `ReportQuestion` — questionId, questionIndex, questionContent, answerContent, feedbackContent, score, `keywords(List<String>)`, `followUps(List<FollowUpQuestion>)`
- `FollowUpQuestion` — questionId, questionContent, answerContent, feedbackContent
- `InterviewSession` — sessionStatus(COMPLETED), completedAt (totalScore는 InterviewSession이 아닌 InterviewReport에 저장)
- `Question`, `Answer`, `Feedback` — 전체 히스토리 조회 대상
- 프롬프트 파일: `resources/prompts/summary.st`
  - LLM 반환 구조: `{ totalScore, overallComment, questions[{ questionId, keywords[] }] }`
- `ReportGenerator` 인터페이스 — `ApiReportGenerator`(prod), `MockReportGenerator`(!prod)
- `ReportPromptBuilder` — `Interview` 도메인으로부터 summary 프롬프트 조립
- `ReportQuestionAssembler` — LLM 결과 + DB 질문·답변·피드백 병합, 꼬리 질문 체인 재귀 구성
