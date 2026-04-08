# 면접 API 시퀀스 플로우 (서버 상태 관리)

## 설계 원칙

- `currentMainQuestionIdx`, `followUpCount`는 서버(DB)가 관리 — 클라이언트가 인덱스를 전달하지 않음
- `conversationId`는 `"{sessionId}"`로 세션 단위 고정 사용 — DB 저장 불필요
- 꼬리질문(꼬리질문의 꼬리질문 포함)은 같은 세션의 conversationId를 공유
- **서버가 꼬리질문/본 질문 분기를 결정** — 프론트는 단순 채팅 루프만 유지
- 꼬리질문 스킵 기능 없음
- 세션 생성과 질문 생성은 별도 API로 분리 — 질문 생성은 Claude API 호출로 수 초 소요

---

## 액터

- Frontend (React + TypeScript)
- Backend (Spring Boot)
- Claude API (질문 생성 / 피드백+꼬리질문)

---

## ① 면접 설정 및 세션 생성

```
Frontend → Backend  POST /api/interviews
  body: { interviewType, difficulty, questionCount, interviewerTone, csSubjects?, portfolioLinks? }
  → Interview 생성 (maxFollowUpCount = 3 고정)
  ← 201 { interviewId }

Frontend → Backend  POST /api/interviews/{interviewId}/sessions
  → InterviewSession 생성 (currentMainQuestionIdx = 0, followUpCount = 0)
  ← 201 { sessionId }
```

---

## ② 질문 일괄 생성

```
Frontend → Backend  POST /api/interviews/{interviewId}/questions
  → Claude API에 질문 {questionCount}개 일괄 생성 요청
  → Question[] DB 저장 (questionType: QUESTION, questionIndex: 0, 1, 2, ...)
  ← 201 { questions[] }
```

**설계 결정**
- 세션 생성과 분리 — 질문 생성 실패 시 세션은 유지되어 재시도 가능
- 질문 목록은 프론트에 내려주지만 화면에 노출하지 않음

---

## ③ 현재 질문 조회 (부작용 없음)

```
Frontend → Backend  GET /api/interviews/{interviewId}/questions/current

  [서버 로직]
  followUpCount > 0  → 최신 FOLLOW_UP 질문 반환
  followUpCount == 0 → questionIndex = currentMainQuestionIdx인 QUESTION 반환

  ← 200 { questionId, question, questionType, hasNext }
```

- `questionType`: `QUESTION` / `FOLLOW_UP` — 프론트 UI 분기용
- `hasNext`: 다음 본 질문 존재 여부
- 반복 호출해도 상태 변경 없음 (새로고침 안전)

---

## ④ 답변 제출 → 서버가 다음 질문 결정

```
Frontend → Backend  POST /api/interviews/{interviewId}/answers
  body: { questionId, content }

  Backend → Claude API: 피드백 + 꼬리질문 요청
    - 프롬프트: feedback-followup.st
    - conversationId: "{sessionId}"
  Claude API → Backend: { feedback, followUpQuestion }

  Backend → DB: 답변 + 피드백 저장

  [서버 분기]
  followUpQuestion 존재 && followUpCount < maxFollowUpCount(3)
    → DB: 꼬리질문을 Question으로 저장 (questionType: FOLLOW_UP), followUpCount++
  그 외 (꼬리질문 없음 || 최대 개수 초과)
    → DB: currentMainQuestionIdx++, followUpCount = 0

  ← 200 { feedback }
```

프론트는 피드백 수신 후 `GET /questions/current` 호출 → 다음 질문 표시

---

## 프론트엔드 루프

```
GET  /questions/current   → 질문 표시 (questionType으로 UI 구분)
POST /answers             → 피드백 표시
GET  /questions/current   → 다음 질문 표시 (꼬리질문 or 본 질문)
...반복
```

---

## ⑤ 세션 종료 & 종합 리포트

```
Frontend → Backend  POST /api/interviews/{interviewId}/finish
  → DB: 전체 history + feedbacks 조회
  → Claude API: 종합 평가 리포트 요청 (score-report.st)
  → DB: status = COMPLETED 업데이트
  ← 200 { scoreReport, questions[{ question, answer, feedback }] }
```

---

## InterviewSession 필드

| 필드 | 타입 | 기본값 | 설명 |
|------|------|--------|------|
| `currentMainQuestionIdx` | int | 0 | 현재 본 질문 인덱스 |
| `followUpCount` | int | 0 | 현재 본 질문의 꼬리질문 누적 수 |

## Interview 필드

| 필드 | 타입 | 기본값 | 설명 |
|------|------|--------|------|
| `maxFollowUpCount` | int | 3 | 본 질문당 꼬리질문 최대 허용 수 |

---

## 엔드포인트 요약

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/interviews` | 면접 설정 생성 |
| POST | `/api/interviews/{interviewId}/sessions` | 세션 생성 |
| POST | `/api/interviews/{interviewId}/questions` | 질문 일괄 생성 (Claude API) |
| GET  | `/api/interviews/{interviewId}/questions/current` | 현재 질문 조회 (부작용 없음) |
| POST | `/api/interviews/{interviewId}/answers` | 답변 제출 → 피드백, 서버가 다음 질문 결정 |
| POST | `/api/interviews/{interviewId}/finish` | 세션 종료 + 종합 리포트 |
