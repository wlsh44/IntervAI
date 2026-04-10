# 세션 기록 및 히스토리

## 개요
- 목적: 면접 세션의 대화 전체를 영구 저장하고, 사용자가 과거 세션을 조회·검색할 수 있도록 한다.
- 대상 사용자: 이전 면접 연습 기록을 다시 확인하고 싶은 사용자
- 관련 개발 단계: Stage 5 (세션 기록 저장 및 히스토리 UI) — 미구현

## 진행 상황

| 단계 | 상태 |
|------|------|
| Stage 5 — 세션 기록 저장 및 히스토리 UI | ⬜ 예정 |

## 요구사항
### 기능 요구사항
- [ ] 세션 목록 조회 (날짜, 면접 유형, 상태 표시)
- [ ] 세션 상세 조회 (질문, 답변, 피드백 전체 내용)
- [ ] 키워드 검색 (세션 내 질문/답변 기반)
- [ ] 날짜 필터
- [ ] 면접 유형 필터
- [ ] 세션 삭제 (soft delete)

### 비기능 요구사항
- [ ] 세션 목록 조회 시 페이지네이션 적용
- [ ] 검색 및 필터 성능을 위한 인덱스 설계

## 사용자 스토리
- As a 로그인된 사용자, I want to 지난 면접 세션 목록을 한눈에 보고 싶다 so that 내 면접 연습 이력을 빠르게 파악할 수 있다.
- As a 로그인된 사용자, I want to 특정 키워드로 과거 세션을 검색하고 싶다 so that 관련 질문이나 답변을 빠르게 찾을 수 있다.
- As a 로그인된 사용자, I want to 불필요한 세션을 삭제하고 싶다 so that 히스토리를 깔끔하게 관리할 수 있다.

## 사용자 플로우
1. 사용자가 로그인 후 히스토리 페이지로 이동한다.
2. 세션 목록이 날짜 내림차순으로 표시된다 (날짜, 면접 유형, 상태).
3. 특정 세션을 클릭하면 해당 세션의 전체 대화 내용(질문, 답변, 피드백)을 볼 수 있다.
4. 키워드 검색창에 검색어를 입력하여 관련 세션을 필터링한다.
5. 날짜 또는 면접 유형으로 필터를 적용한다.
6. 세션 삭제 버튼 클릭 시 확인 후 해당 세션을 삭제한다.

## 수용 기준 (Acceptance Criteria)
- [ ] `GET /api/interviews` — 세션 목록 API, 날짜 내림차순 정렬, 페이지네이션 지원
- [ ] `GET /api/interviews/{interviewId}` — 세션 상세 조회 API, 전체 질문·답변·피드백 반환
- [ ] 키워드 검색 파라미터로 세션 필터링
- [ ] 날짜 범위 및 면접 유형 파라미터로 필터링
- [ ] `DELETE /api/interviews/{interviewId}` — 세션 삭제 API, soft delete 방식 처리
- [ ] 타인의 세션 접근 시 `INTERVIEW_ACCESS_DENIED` (403) 반환

## 범위 외 (Out of Scope)
- 세션 공유 (타인에게 세션 공유)
- 세션 내보내기 (PDF, CSV 등)
- 세션 간 비교 기능
- 세션 이어하기 (IN_PROGRESS 상태 재개) — Stage 4 범위 외

## 미결 사항 (Open Questions)
- 세션 목록 한 페이지당 노출 건수 결정
- 검색 범위 결정 (질문 내용 vs 답변 내용 vs 전체)
- 세션 데이터 보관 기간 정책 (무기한 vs 일정 기간 후 자동 삭제)

## 관련 도메인/엔티티
- `Interview` — id, userId, interviewType, difficulty, completedAt
- `InterviewSession` — id, interviewId, sessionStatus (IN_PROGRESS / COMPLETED), currentMainQuestionIdx, completedAt
- `Question` — id, sessionId, content, questionType
- `Answer` — id, questionId, content
- `Feedback` — id, answerId, content
