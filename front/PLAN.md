# PLAN.md — Issue #21 면접 히스토리 FE

## 개요
- 이슈: #21 — 면접 히스토리 목록 조회 및 삭제 (FE)
- 브랜치: `fe/feat/history`
- Stitch 화면: "면접 히스토리 (정보 계층 최적화)" (screen: `347d8229d892469294f35b1e8b76337c`)
- 백엔드 경로: `src/main/java/wlsh/project/intervai/interview/`

## 구현 목표
`/history` 경로의 `HistoryPage.tsx` placeholder를 완전한 히스토리 페이지로 구현.
필터(키워드/날짜/유형/상태) + 카드 목록 + 페이지네이션 + 삭제 기능.

---

## 영향 받는 파일

| 파일 | 변경 유형 |
|------|---------|
| `front/src/shared/api/constants.ts` | 수정 — `interviews.delete` path 추가 |
| `front/src/features/dashboard/api/dashboardApi.ts` | 수정 — `InterviewSummary.totalScore` 필드 추가 |
| `front/src/shared/types/queryKeys.ts` | 수정 — `interviews.list(params?)` 파라미터 지원 |
| `front/src/features/history/api/historyApi.ts` | 신규 — 히스토리 조회/삭제 API 함수 |
| `front/src/features/history/hooks/useInterviewHistory.ts` | 신규 — 필터+페이지네이션 쿼리 훅 |
| `front/src/features/history/hooks/useDeleteInterview.ts` | 신규 — 삭제 mutation 훅 |
| `front/src/features/history/components/HistoryFilterBar.tsx` | 신규 — 필터 바 컴포넌트 |
| `front/src/features/history/components/HistoryCard.tsx` | 신규 — 면접 카드 컴포넌트 |
| `front/src/shared/pages/HistoryPage.tsx` | 수정 — placeholder → 완전 구현 |

---

## 작업 단계

### Step 1: 브랜치 생성
```bash
git checkout -b fe/feat/history
```

### Step 2: API constants 업데이트
**`front/src/shared/api/constants.ts`**
`API_PATHS.interviews`에 추가:
```ts
delete: (interviewId: number) => `/api/interviews/${interviewId}`,
```

### Step 3: dashboardApi.ts 타입 업데이트
**`front/src/features/dashboard/api/dashboardApi.ts`**
`InterviewSummary`에 추가:
```ts
totalScore: number | null
```

### Step 4: queryKeys 업데이트
**`front/src/shared/types/queryKeys.ts`**
```ts
interviews: {
  all: ['interviews'] as const,
  list: (params?: Record<string, unknown>) => ['interviews', 'list', params] as const,
}
```
→ 대시보드 훅: `queryKeys.interviews.list()` → `['interviews', 'list', undefined]` (영향 없음)
→ 히스토리 훅: `queryKeys.interviews.list({...filters})` → 별도 캐시 키

### Step 5: historyApi.ts 생성
**신규: `front/src/features/history/api/historyApi.ts`**

```ts
export interface HistoryListParams {
  page?: number
  size?: number
  keyword?: string
  startDate?: string        // yyyy-MM-dd
  endDate?: string          // yyyy-MM-dd
  interviewType?: InterviewType
  status?: SessionStatus
}

getInterviewHistory(params?: HistoryListParams): Promise<InterviewListResponse>
deleteInterview(interviewId: number): Promise<void>
```
- `InterviewListResponse`와 타입은 `dashboardApi.ts`에서 import (중복 선언 금지)

### Step 6: hooks 생성
**신규: `front/src/features/history/hooks/useInterviewHistory.ts`**
- `filters` + `page` → `queryKeys.interviews.list({...filters, page})`
- `staleTime: 60_000`, `retry: false`

**신규: `front/src/features/history/hooks/useDeleteInterview.ts`**
- `useMutation` → `deleteInterview(interviewId)`
- 성공 시 `invalidateQueries({ queryKey: queryKeys.interviews.all })` (대시보드 포함 갱신)

### Step 7: HistoryFilterBar 컴포넌트
**신규: `front/src/features/history/components/HistoryFilterBar.tsx`**
- `keyword` 검색 input (변경 즉시 반영)
- `startDate` / `endDate` date input
- `interviewType` select: 전체 / CS / PORTFOLIO / ALL
- `status` select: 전체 / 완료(COMPLETED) / 진행 중(IN_PROGRESS)
- 필터 변경 시 부모로 콜백 (page를 0으로 리셋은 부모에서 처리)

### Step 8: HistoryCard 컴포넌트
**신규: `front/src/features/history/components/HistoryCard.tsx`**
- 유형 배지 (CS 기초 / 포트폴리오 / 종합) + 난이도
- 날짜 (`formatDate(createdAt)`) + 질문 수 (`{n}문항`)
- AI SCORE: `totalScore != null ? totalScore + '/100' : '--'`
- 상태 배지 (완료 / 진행 중)
- 액션 버튼: `COMPLETED` → "결과 보기" (`/interviews/{id}/result`), `IN_PROGRESS` → "이어하기" (`/interview/{id}`)
- 삭제 버튼: `window.confirm` 후 `deleteInterview` 호출

### Step 9: HistoryPage 구현
**수정: `front/src/shared/pages/HistoryPage.tsx`**
- `useState`로 `filters` + `page` 관리
- `useInterviewHistory(filters, page)` 데이터 패치
- 레이아웃: 페이지 제목 "면접 기록" + HistoryFilterBar + 카드 목록 + 페이지네이션
- 빈 목록: "면접 기록이 없습니다" 안내 텍스트
- 로딩: 간단한 텍스트 ("불러오는 중...")

---

## 예상 커밋 단위

| # | 커밋 메시지 | 포함 파일 |
|---|------------|---------|
| 1 | `feat: history API, hooks, type 추가 (#21)` | constants.ts, dashboardApi.ts, queryKeys.ts, historyApi.ts, useInterviewHistory.ts, useDeleteInterview.ts |
| 2 | `feat: HistoryFilterBar, HistoryCard 컴포넌트 추가 (#21)` | HistoryFilterBar.tsx, HistoryCard.tsx |
| 3 | `feat: HistoryPage 구현 (#21)` | HistoryPage.tsx |

---

## 주의사항
- `totalScore` null → `--` 표시 (issue-21.md 명시)
- PORTFOLIO 유형은 keyword 검색 미지원 — 백엔드 의도된 동작, UI 별도 안내 불필요
- 삭제 후 대시보드 recent history도 자동 갱신됨 (`invalidateQueries(interviews.all)`)
- 브랜치 생성은 첫 번째 파일 수정 전에 반드시 먼저 실행 (Agent Trap)
