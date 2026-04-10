# 프런트엔드 아키텍처 & 개발 가이드

## 디렉터리 구조

```
src/
├── app/              # 진입점, 라우터, Provider
├── features/         # 도메인별 기능
│   └── {feature}/
│       ├── api/          # API 호출 함수
│       ├── hooks/        # useQuery / useMutation
│       ├── stores/       # Zustand 스토어
│       ├── components/   # 도메인 전용 컴포넌트
│       └── pages/        # 라우트 단위 페이지
└── shared/           # 공통 코드
    ├── api/              # axios 인스턴스
    ├── components/
    │   ├── ui/           # shadcn/ui 컴포넌트
    │   └── layout/       # AppLayout, Sidebar
    ├── hooks/
    └── types/            # api.ts, enums.ts, queryKeys.ts
```

### 규칙
- `features/` 간 직접 import 금지 — 공통 코드는 `shared/`로 이동
- `pages/`는 layout 조합 + hooks 호출만. 비즈니스 로직 금지
- 컴포넌트 1파일 = 1컴포넌트
- shadcn/ui 컴포넌트는 `shared/components/ui/`에 설치

---

## 상태관리 전략

### 서버 상태 → TanStack Query
API 응답 데이터는 전부 TanStack Query로 관리. Zustand에 서버 데이터 저장 금지.

```
useQuery   → GET 요청 (프로필 조회, 현재 질문 조회)
useMutation → POST/PUT 요청 (로그인, 답변 제출 등)
```

### 클라이언트 상태 → Zustand

| 스토어 | 저장 항목 |
|--------|----------|
| `authStore` | `accessToken`, `userId`, `nickname` |
| `interviewStore` | `interviewId`, `sessionId`, `phase` |

- `phase`: `setup | generating | chat | finished`
- Zustand `persist`는 `accessToken`에 절대 사용 금지

---

## 인증 처리

### accessToken — 메모리 전용
- `authStore.accessToken`에만 저장 (localStorage / sessionStorage 금지)
- 페이지 리프레시 시 `POST /api/auth/refresh`로 복구

### refreshToken — HttpOnly 쿠키
- JS에서 접근 불가, 브라우저가 자동 전송
- axios `withCredentials: true` 필수

### Silent Refresh (response interceptor)
```
401 수신
  → POST /api/auth/refresh (_retry 플래그로 무한 루프 방지)
  → 성공: accessToken 갱신 → 원래 요청 재시도
  → 실패: authStore 초기화 → /login 리다이렉트
```

### 앱 초기화
`App.tsx` 마운트 시 `accessToken`이 없으면 refresh 1회 호출. 실패 시 `/login`.

---

## API 연동

### axios 인스턴스 (`shared/api/httpClient.ts`)
```
baseURL: VITE_API_BASE_URL  (기본: http://localhost:8080)
withCredentials: true
request interceptor:  Authorization: Bearer {accessToken} 자동 추가
response interceptor: 401 → silent refresh
```

### TanStack Query Key 구조
```typescript
export const queryKeys = {
  profile: {
    detail: (profileId: number) => ['profile', profileId] as const,
  },
  interview: {
    currentQuestion: (interviewId: number) =>
      ['interview', interviewId, 'currentQuestion'] as const,
  },
}
```

### QueryClient 설정
```
queries.retry: 1
queries.staleTime: 60_000
mutations.retry: 0
```

---

## 에러 핸들링

| 상황 | 처리 위치 | 처리 방법 |
|------|----------|----------|
| 401 | httpClient interceptor | silent refresh → 재시도 |
| 403 | PrivateRoute / 페이지 | /dashboard 리다이렉트 |
| 400 / 404 / 500 | 각 hook `onError` | 토스트 메시지 |
| LLM 타임아웃 | hook `onError` | "AI 응답 지연" 안내 토스트 |

에러 응답 형식: `{ code: string, message: string }`

---

## 면접 플로우 구현 가이드

API 호출 순서를 반드시 지켜야 함 (순서 위반 시 `SESSION_NOT_FOUND` 에러).

```
1. POST /api/interviews                        → interviewId 저장
2. POST /api/interviews/{id}/sessions          → sessionId 저장
3. POST /api/interviews/{id}/questions         ← LLM, 수 초 소요
4. GET  /api/interviews/{id}/questions/current → 현재 질문 표시
5. POST /api/interviews/{id}/answers           ← LLM, 수 초 소요
   → feedback 표시 (기본 숨김, "피드백 보기" 클릭 시 노출)
   → hasNext: true  → 4번으로
   → hasNext: false → POST /api/interviews/{id}/finish
```

- LLM 호출(3, 5번) 시 `isPending: true` 동안 제출 버튼 `disabled` + 스피너 필수
- `interviewStore.phase`로 현재 단계 관리, 각 hook의 `enabled` 옵션으로 조기 호출 방지

---

## 코드 컨벤션

- TypeScript strict 모드, `any` 사용 금지
- 함수형 컴포넌트 + 화살표 함수
- 컴포넌트 PascalCase, 훅 camelCase (`use` 접두사), 타입 PascalCase
- import 순서: 외부 라이브러리 → shared → features 내부

---

## 환경변수

```
# .env.development
VITE_API_BASE_URL=http://localhost:8080
```
