# 종합 리포트

## 개요
- 목적: 면접 세션 종료 후 LLM이 전체 대화를 분석하여 종합 점수, 강점/약점, 개선 방향을 요약한 리포트를 제공한다.
- 대상 사용자: 면접 세션을 완료한 사용자
- 관련 개발 단계: Stage 6 (종합 리포트 + 포트폴리오 기반 질문 고도화) — 미구현

## 진행 상황

| 단계 | 상태 |
|------|------|
| Stage 6 — 종합 리포트 + 포트폴리오 기반 질문 고도화 | ⬜ 예정 |

## 요구사항
### 기능 요구사항
- [ ] 세션 종료 시 전체 질문·답변·피드백 히스토리를 기반으로 종합 평가 리포트 생성
- [ ] 종합 점수(totalScore) 산출
- [ ] 세부 점수 항목 평가
  - [ ] 개념 이해도 (conceptUnderstanding)
  - [ ] 문제 해결력 (problemSolving)
  - [ ] 커뮤니케이션 (communication)
- [ ] 강점(strengths) 목록 제공
- [ ] 개선 방향(improvements) 목록 제공
- [ ] 종합 코멘트(overallComment) 제공
- [ ] 세션 내 각 질문별 질문·답변·피드백 요약 제공
- [ ] 종합 리포트 결과 DB 저장

### 비기능 요구사항
- [ ] 리포트 생성은 `summary.st` 프롬프트 파일을 사용 (현재 파일 존재, 내용 미확정)
- [ ] 세션 상태는 `POST /api/interviews/{interviewId}/finish` 시점에 COMPLETED로 업데이트됨
  - 현재 finish API는 COMPLETED 업데이트만 처리하며 리포트 생성은 미구현

## 사용자 스토리
- As a 면접을 완료한 사용자, I want to 내 전체 면접 성과를 종합적으로 평가받고 싶다 so that 내 강점과 약점을 객관적으로 파악할 수 있다.
- As a 면접을 완료한 사용자, I want to 구체적인 개선 방향을 제안받고 싶다 so that 다음 면접 준비에 활용할 수 있다.
- As a 면접을 완료한 사용자, I want to 각 질문별 내 답변과 피드백을 한눈에 볼 수 있고 싶다 so that 어떤 부분이 부족했는지 상세하게 복기할 수 있다.

## 사용자 플로우
1. 사용자가 모든 질문에 답변을 마치고 "세션 종료" 버튼을 누른다.
2. Backend가 해당 세션의 전체 히스토리(질문·답변·피드백)를 조회한다.
3. Backend가 `summary.st` 프롬프트로 Claude API에 종합 평가 요청을 보낸다.
   - 변수: `{ interviewType, difficulty, interviewerTone, history }`
4. Claude API가 종합 평가 결과를 반환한다.
5. Backend가 세션 상태를 COMPLETED로 업데이트하고 totalScore를 저장한다.
6. 프론트엔드에 종합 리포트와 질문별 요약을 반환한다.
7. 사용자는 리포트 페이지에서 점수, 강점, 개선 방향, 각 질문·답변·피드백을 확인한다.

## 수용 기준 (Acceptance Criteria)
- [ ] `POST /api/interviews/{interviewId}/finish` 응답에 종합 리포트 포함
  - 현재 finish API는 200 OK (body 없음) 반환 — 리포트 통합 시 응답 구조 변경 필요
- [ ] 응답에 `totalScore`, `scores(conceptUnderstanding, problemSolving, communication)` 포함
- [ ] 응답에 `strengths[]`, `improvements[]`, `overallComment` 포함
- [ ] 응답에 `questions[{ question, answer, feedback }]` 목록 포함
- [ ] 세션 상태가 COMPLETED로 업데이트됨 (이미 구현)
- [ ] `totalScore`가 `InterviewSession`에 저장됨 (`InterviewSessionEntity` 필드 추가 필요)

## 범위 외 (Out of Scope)
- 여러 세션 간 성장 추이 그래프/비교 분석
- 리포트 공유 기능
- PDF 내보내기
- 특정 약점 기반 자동 재시험 추천

## 미결 사항 (Open Questions)
- 점수 산출 기준 및 척도 결정 (0~100점 vs 1~5점 등)
- 세부 점수 항목 추가 또는 변경 가능성
- 리포트 생성 실패(LLM 오류) 시 세션 종료 처리 방법
- finish API 응답 구조 변경 범위 (리포트 데이터 추가 vs 별도 조회 API 분리)
- `InterviewSession`에 totalScore 필드 추가 여부 및 타입 결정

## 관련 도메인/엔티티
- `InterviewSession` — totalScore (미구현), sessionStatus (COMPLETED), completedAt
- `Question`, `Answer`, `Feedback` — 전체 히스토리 조회 대상
- 프롬프트 파일: `resources/prompts/summary.st`
- 리포트 응답 구조 (예정):
  ```
  scoreReport:
    totalScore
    scores: { conceptUnderstanding, problemSolving, communication }
    strengths[]
    improvements[]
    overallComment
  questions[]: { question, answer, feedback }
  ```
