# 프런트엔드 개발 히스토리

프런트엔드 개발 중 발생한 문제 상황과 해결 방법을 기록합니다.

---

## 2026-04-09 — 인증 페이지 구현

### 문제 1: Tailwind CSS가 적용되지 않음

**상황**: `npm run dev` 실행 후 auth 페이지에 Tailwind 클래스가 전혀 적용되지 않음.

**원인**: 두 가지 누락
1. `vite.config.ts`에 `@tailwindcss/vite` 플러그인 미등록
2. `src/index.css`에 `@import "tailwindcss"` 누락 (Tailwind v4는 `@tailwind base/components/utilities` 대신 이 방식 사용)

**해결**:
```ts
// vite.config.ts
import tailwindcss from '@tailwindcss/vite'
plugins: [react(), tailwindcss()]
```
```css
/* src/index.css */
@import "tailwindcss";
```

---

### 문제 2: 로그인 401 응답이 silent refresh 흐름으로 진입

**상황**: 잘못된 자격증명으로 로그인 시도 시 401 응답이 `httpClient`의 response interceptor를 타서 무한 refresh 시도 발생.

**원인**: `authApi.ts`가 interceptor가 붙은 `httpClient`를 사용하고 있었음. 로그인/회원가입은 인증 전 호출이므로 interceptor를 거치면 안 됨.

**해결**: 인증 API 전용 `authClient` 인스턴스 분리.
```ts
// authApi.ts
const authClient = axios.create({ baseURL: BASE_URL, withCredentials: true })
// interceptor 없음 — refresh 흐름 우회
```

---

### 문제 3: 로그인 폼 유효성 검사가 너무 엄격

**상황**: Codex 리뷰에서 로그인 폼에 회원가입과 동일한 4~12자 제한이 적용되어 있다고 지적. 로그인은 자격증명 확인이 목적이므로 길이 제한이 불필요.

**해결**: `loginSchema`를 `min(1)` (입력 여부 확인)으로만 변경.

---

### 문제 4: 잘못된 Stitch 프로젝트 참조

**상황**: Stitch MCP로 생성된 디자인이 실제 사용 중인 프로젝트와 달랐음. 다크 테마로 생성됐으나 실제 프로젝트(`6025092785962214042`)는 라이트 인디고 테마.

**해결**: 올바른 프로젝트 ID(`6025092785962214042`)로 연결 후 스크린 HTML을 직접 fetch하여 색상 토큰 추출. `src/index.css`의 `@theme` 블록을 라이트 테마로 전면 교체.

---

### 문제 5: `#root` CSS가 auth 페이지 레이아웃을 깨뜨림

**상황**: `src/index.css`의 `#root`에 `width: 1126px`, `border-inline`, `text-align: center` 등이 적용되어 있어 auth 페이지의 full-screen 레이아웃이 깨짐.

**해결**: `#root` 스타일을 `min-height: 100svh; display: flex; flex-direction: column;`만 남기고 제거.

---

## 2026-04-09 — 개발 워크플로우 반자동화

### 문제 6: 프런트엔드 개발 워크플로우가 매번 수동 반복

**상황**: 매 도메인 개발마다 8단계 워크플로우(브랜치 이동 → 디자인 조회 → 플래너 → 구현 → 리뷰 → 커밋 → PR → 리뷰 반영)를 수동으로 하나씩 실행해야 했음.

**해결**: 4개의 slash command + 2개의 sub-agent로 반자동화.

| 커맨드 | 역할                               |
|--------|----------------------------------|
| `/fe-start {domain} {screen-id}` | 브랜치 + Stitch 디자인 조회 + PLAN.md 생성 |
| `/fe-implement` | 구현 + codex 리뷰                    |
| `/fe-ship` | 논리적 커밋 분리 + push + PR            |
| `/fe-review` | 리뷰 스레드 분류 + 수정 + resolve         |

| 에이전트 | 역할 |
|---------|------|
| `fe-dev-planner` | Stitch 디자인 분석 + 코드 현황 파악 + PLAN.md 작성 |
| `fe-plan-executor` | PLAN.md 단계별 구현 + typecheck 자동 검증 |

---

### 프런트엔드 변경 (fe/feat/profile)

- `createProfile` API 함수 삭제
- `useCreateProfile` 훅 삭제
- `ProfilePage`에서 프로필 생성 로직 제거 (회원가입 시 자동 생성이므로 바로 조회)
- `ProfileResponse.updatedAt` 필드 반영 — 최근 업데이트 날짜 표시
- 포트폴리오 링크 중복 추가 방지 버그 수정
- 기술 스택 쉼표 입력 시 태그명에 쉼표 포함되는 버그 수정
