# 대시보드 페이지 개발 계획

## 요구사항

- 로그인한 사용자를 닉네임으로 맞이하는 웰컴 섹션 표시
- "면접 시작하기" 버튼으로 `/interview` 이동
- 통계 카드: 현재 API 기준으로 표시 가능한 항목만 (총 세션 수, 최근 면접일)
- 최근 면접 히스토리 최대 3개 표시 (날짜, 면접 유형, 세션 상태)
- "더보기" 링크로 `/history` 이동 (라우트 등록 포함)
- `GET /api/interviews` API가 api.md에 없으므로 스펙 추가 필요

---

## 디자인 요약 (Stitch 스크린)

스크린 ID: `29a9aaf214ce4f8d8579065786ecdb77`

- 레이아웃: 기존 AppLayout 사이드바 + 우측 메인 콘텐츠 영역. 콘텐츠 배경 `#faf8ff`.
- 색상 토큰:
  - Primary: `#4648d4`
  - Secondary: `#6b38d4`
  - Background: `#faf8ff`
  - Surface/Card: `#eaedff`, `#dae2fd`, `#e2e7ff`
  - Text dark: `#131b2e`
  - Text on-primary: `#ffffff`
- 컴포넌트:
  - 웰컴 카드 (gradient 버튼 포함)
  - 통계 카드 2칸 그리드 (총 면접 횟수, 최근 면접일)
  - 최근 면접 기록 카드 리스트 (chevron 포함)
- 텍스트:
  - 헤드라인: `{nickname}님, 반갑습니다!`
  - 서브텍스트: `꾸준한 연습이 합격을 만듭니다. 오늘도 함께해요.`
  - 웰컴 CTA: `면접 시작하기`
  - 섹션 제목: `최근 면접 기록`
  - 더보기 링크: `더보기`
  - 통계 레이블: `총 면접 횟수`, `최근 면접일`
  - 빈 상태: `아직 면접 기록이 없습니다`

---

## 현재 구현 상태

| 파일 | 상태 |
|------|------|
| `front/src/shared/pages/DashboardPage.tsx` | stub — "구현 예정" 텍스트만 존재 |
| `front/src/shared/api/constants.ts` | `interviews` 경로 없음 |
| `front/src/shared/types/queryKeys.ts` | `interviews.list` 키 없음 |
| `front/src/shared/types/enums.ts` | `SessionStatus` 미정의 |
| `front/src/app/router.tsx` | `/history` 라우트 미등록 |
| `docs/api.md` | `GET /api/interviews` 스펙 없음 |
| `front/src/features/dashboard/` | 디렉토리 없음 |

---

## 구현 필요 항목

1. **docs/api.md 수정**: `GET /api/interviews` 스펙 추가 (session-history.md 수용 기준 기반)
2. **constants.ts 수정**: `API_PATHS.interviews.list` 경로 추가
3. **enums.ts 수정**: `SessionStatus` enum 추가 (`IN_PROGRESS`, `COMPLETED`)
4. **queryKeys.ts 수정**: `interviews.list()` 쿼리 키 추가
5. **dashboard feature 디렉토리 생성**: `api/`, `hooks/`, `components/`
6. **dashboardApi.ts 생성**: `GET /api/interviews` 호출 함수 및 타입 정의
7. **useInterviewList.ts 생성**: TanStack Query `useQuery` 훅
8. **WelcomeSection 컴포넌트**: 닉네임 + CTA 버튼
9. **StatsSection 컴포넌트**: 통계 카드 2개 (총 세션 수, 최근 면접일)
10. **RecentHistorySection 컴포넌트**: 최근 3개 세션 카드 + 빈 상태
11. **DashboardPage.tsx 수정**: 위 컴포넌트 조합
12. **router.tsx 수정**: `/history` 라우트 등록 (stub 페이지 생성 포함)

---

## 개발 계획

### Step 1: 문서 및 공유 타입/상수 업데이트

- [ ] `docs/api.md`에 `GET /api/interviews` 스펙 추가
  - Query Parameters: `page` (Integer, default 0), `size` (Integer, default 10)
  - Response 200: `{ content: InterviewSummary[], totalElements: number, totalPages: number, last: boolean }`
  - `InterviewSummary` 필드: `id` (Long), `interviewType` (InterviewType), `difficulty` (Difficulty), `questionCount` (Integer), `sessionStatus` (SessionStatus: IN_PROGRESS | COMPLETED), `createdAt` (ISO 8601)
  - 에러: 없음 (빈 배열 반환)
- [ ] `front/src/shared/types/enums.ts`에 `SessionStatus` 추가
  ```ts
  export const SessionStatus = {
    IN_PROGRESS: 'IN_PROGRESS',
    COMPLETED: 'COMPLETED',
  } as const
  export type SessionStatus = (typeof SessionStatus)[keyof typeof SessionStatus]
  ```
- [ ] `front/src/shared/api/constants.ts`에 `interviews` 경로 추가
  ```ts
  interviews: {
    list: '/api/interviews',
  },
  ```
- [ ] `front/src/shared/types/queryKeys.ts`에 `interviews` 키 추가
  ```ts
  interviews: {
    all: ['interviews'] as const,
    list: () => ['interviews', 'list'] as const,
  },
  ```

### Step 2: dashboard feature — API 함수 및 타입

- [ ] `front/src/features/dashboard/api/dashboardApi.ts` 생성
  - `InterviewSummary` 인터페이스: `id`, `interviewType: InterviewType`, `difficulty: Difficulty`, `questionCount: number`, `sessionStatus: SessionStatus`, `createdAt: string`
  - `InterviewListResponse` 인터페이스: `content: InterviewSummary[]`, `totalElements: number`, `totalPages: number`, `last: boolean`
  - `getInterviewList(params?: { page?: number; size?: number }): Promise<InterviewListResponse>` — `httpClient.get` + `API_PATHS.interviews.list` 사용

### Step 3: dashboard feature — Query 훅

- [ ] `front/src/features/dashboard/hooks/useInterviewList.ts` 생성
  - `useQuery` 사용, `queryKey: queryKeys.interviews.list()`
  - `queryFn: () => getInterviewList({ size: 3 })`
  - `staleTime: 60_000` (1분 캐시)
  - 에러 시 retry 없이 빈 상태 표시할 수 있도록 `retry: false`

### Step 4: dashboard feature — UI 컴포넌트

- [ ] `front/src/features/dashboard/components/WelcomeSection.tsx` 생성
  - `useAuthStore((s) => s.nickname)`으로 닉네임 조회
  - `Link` (`react-router-dom`)으로 `/interview` 이동하는 버튼
  - 버튼 스타일: `style={{ background: 'linear-gradient(135deg, #4648d4, #6b38d4)' }}` + `className="text-white rounded-lg px-6 py-3 font-semibold hover:opacity-90 transition-opacity"`
  - 카드 배경: `bg-[#eaedff] rounded-2xl p-8`
  - 헤드라인: `text-[#131b2e] text-2xl font-bold`
  - 서브텍스트: `text-[#131b2e]/70 mt-2`

- [ ] `front/src/features/dashboard/components/StatsSection.tsx` 생성
  - Props: `data: InterviewListResponse | undefined`
  - 카드 2개 (`grid grid-cols-2 gap-4`):
    1. 총 면접 횟수: `data?.totalElements ?? 0` + 단위 "회"
    2. 최근 면접일: `data?.content[0]?.createdAt`을 `YYYY.MM.DD` 포맷으로 변환, 없으면 "-"
  - 각 카드 배경: `bg-[#dae2fd] rounded-xl p-5`
  - 수치 텍스트: `text-2xl font-bold text-[#131b2e]`
  - 레이블 텍스트: `text-sm text-[#131b2e]/60 mt-1`
  - 날짜 포맷 유틸: `new Date(createdAt).toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' })`

- [ ] `front/src/features/dashboard/components/RecentHistorySection.tsx` 생성
  - Props: `items: InterviewSummary[]`
  - `InterviewType` 한글 매핑: `CS → 'CS 기초'`, `PORTFOLIO → '포트폴리오'`, `ALL → '종합'`
  - `SessionStatus` 한글 매핑: `IN_PROGRESS → '진행 중'`, `COMPLETED → '완료'`
  - `SessionStatus` 색상 매핑: `IN_PROGRESS → 'text-amber-600 bg-amber-50'`, `COMPLETED → 'text-green-600 bg-green-50'`
  - 빈 상태 (`items.length === 0`): `text-[#131b2e]/40 text-center py-8 "아직 면접 기록이 없습니다"`
  - 각 카드: `flex items-center justify-between p-4 bg-white rounded-xl border border-[#e2e7ff] hover:bg-[#faf8ff]`
  - chevron: `›` 문자, `text-[#4648d4]`
  - 날짜 포맷: StatsSection과 동일한 패턴
  - 섹션 헤더: 제목 "최근 면접 기록" + `Link to="/history"` "더보기" 우측 정렬

### Step 5: DashboardPage 수정 및 라우터 업데이트

- [ ] `front/src/shared/pages/DashboardPage.tsx` 수정
  - `useInterviewList` 훅 호출
  - 로딩 상태: `<div className="animate-pulse">` 스켈레톤 (각 섹션 높이 유지)
  - 에러 상태: `extractApiError` + `getErrorMessage` 사용하여 에러 메시지 표시
  - 에러여도 WelcomeSection과 StatsSection은 data를 `undefined`로 넘겨 렌더링 유지 (graceful degradation)
  - 페이지 배경: `className="bg-[#faf8ff] p-8 space-y-6 min-h-full"`
  - 컴포넌트 순서: `WelcomeSection` → `StatsSection` → `RecentHistorySection`

- [ ] `front/src/shared/pages/HistoryPage.tsx` 생성 (stub)
  - `"히스토리 (구현 예정)"` 텍스트만 포함
  - 추후 `fe/feat/history` 브랜치에서 완전 구현

- [ ] `front/src/app/router.tsx` 수정
  - `import HistoryPage from '../shared/pages/HistoryPage'` 추가
  - AppLayout 하위에 `<Route path="/history" element={<HistoryPage />} />` 등록

---

## 생성/수정 파일 목록

| 파일 | 작업 | 설명 |
|------|------|------|
| `docs/api.md` | 수정 | `GET /api/interviews` 스펙 추가 |
| `front/src/shared/types/enums.ts` | 수정 | `SessionStatus` enum 추가 |
| `front/src/shared/api/constants.ts` | 수정 | `API_PATHS.interviews.list` 추가 |
| `front/src/shared/types/queryKeys.ts` | 수정 | `interviews.list()` 쿼리 키 추가 |
| `front/src/features/dashboard/api/dashboardApi.ts` | 생성 | 면접 목록 API 함수 + 타입 정의 |
| `front/src/features/dashboard/hooks/useInterviewList.ts` | 생성 | 면접 목록 useQuery 훅 |
| `front/src/features/dashboard/components/WelcomeSection.tsx` | 생성 | 웰컴 + gradient CTA 버튼 섹션 |
| `front/src/features/dashboard/components/StatsSection.tsx` | 생성 | 통계 카드 2개 섹션 |
| `front/src/features/dashboard/components/RecentHistorySection.tsx` | 생성 | 최근 면접 기록 카드 리스트 섹션 |
| `front/src/shared/pages/DashboardPage.tsx` | 수정 | stub → 실제 대시보드 구현 |
| `front/src/shared/pages/HistoryPage.tsx` | 생성 | 히스토리 페이지 stub |
| `front/src/app/router.tsx` | 수정 | `/history` 라우트 추가 |

---

## 주의사항

**아키텍처 규칙**
- `DashboardPage`는 `shared/pages/`에 위치하며 `features/dashboard/` 컴포넌트를 import한다. `shared/pages/`는 feature 컴포넌트를 조합하는 진입점이므로 허용된다.
- `WelcomeSection`이 `features/auth/stores/authStore`를 직접 import하는 것은 인증 상태 읽기 전용이며 코드베이스 전반의 기존 패턴(AppLayout 포함)과 동일하므로 허용된다.
- `features/dashboard/`는 `features/auth/` 이외의 다른 feature를 import하지 않는다.
- `useInterviewList`에서 조회한 서버 상태를 Zustand store에 저장하지 않는다.

**API 미구현 대응**
- `GET /api/interviews`는 session-history.md 기준 예정 API이며 현재 백엔드 미구현 상태다.
- API 404 또는 네트워크 에러 시에도 대시보드 UI가 깨지지 않도록 빈 상태 UI를 반드시 구현한다.
- `useInterviewList`에 `retry: false`를 설정하여 실패 시 즉시 에러 상태로 전환한다.

**통계 카드 범위 제한**
- 평균 점수, 연속 학습일은 현재 API에서 계산 불가하므로 구현하지 않는다.
- 통계 카드는 2개만 구현: 총 면접 횟수(`totalElements`), 최근 면접일(`content[0]?.createdAt`)

**날짜 포맷**
- `createdAt`은 ISO 8601 형식(`2026-04-10T12:34:56`)으로 반환됨 (기존 `updatedAt`과 동일한 패턴)
- `toLocaleDateString('ko-KR', ...)` 사용, 별도 date 라이브러리 추가 불필요

**HistoryPage**
- stub으로만 생성하며 실제 구현은 `fe/feat/history` 브랜치에서 별도 진행한다.

---

## 검증 방법

- `npm run typecheck` — TypeScript 오류 없음
- `npm run lint` — ESLint 오류 없음
- 수동 확인 시나리오:
  1. 로그인 후 대시보드(`/`) 접근 시 authStore의 닉네임이 헤드라인에 표시되는지 확인
  2. "면접 시작하기" 클릭 시 `/interview`로 이동하는지 확인
  3. API 응답 전 로딩 상태(스켈레톤)가 표시되는지 확인
  4. 면접 기록이 없을 때 빈 상태 메시지("아직 면접 기록이 없습니다")가 표시되는지 확인
  5. 면접 기록이 있을 때 최대 3개까지만 표시되는지 확인
  6. "더보기" 클릭 시 `/history`로 이동하는지 확인
  7. `/history` 접근 시 stub 페이지("히스토리 구현 예정")가 렌더링되는지 확인
  8. 미인증 상태에서 `/` 또는 `/history` 직접 접근 시 `/login`으로 리다이렉트되는지 확인
  9. API 에러 발생 시 에러 메시지가 표시되고 다른 섹션은 정상 렌더링되는지 확인
