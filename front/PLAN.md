# 프로필 기능 개발 계획

## 요구사항

- 프로필 생성: `POST /api/users/profile` — JWT userId 기반, body 없음
- 프로필 조회: `GET /api/users/profile` — JWT userId 기반, 본인 프로필만 조회
- 프로필 수정: `PUT /api/users/profile` — JWT userId 기반, path parameter 없음
- 희망 직군 선택: FRONTEND | BACKEND | FULLSTACK | ANDROID | IOS | DEVOPS | DATA_ENGINEER | ML_ENGINEER (필수)
- 경력 수준 선택: ENTRY | JUNIOR | SENIOR (필수)
- 기술 스택: 태그 형식 입력, 1~20개 제한, 추천 태그 제공
- 포트폴리오 링크: URL 형식 입력, 최대 5개 제한
- 에러 처리: PROFILE_NOT_FOUND(404), PROFILE_ALREADY_EXISTS(409), INVALID_INPUT(400)
- 저장 성공 시 토스트 알림 표시

## 디자인 요약 (Stitch 스크린: 프로필 설정 한글화)

레이아웃:
- 좌측: AppLayout의 사이드바 네비게이션 재사용
- 우측: 프로필 폼 (스크롤 가능한 메인 영역)
- 폼 상단에 페이지 타이틀 + 최근 업데이트 타임스탬프

색상 토큰:
- Primary (선택/활성): `#4648d4`
- Secondary: `#6b38d4`
- Tertiary: `#006c49`
- Error: `#ba1a1a`
- Surface (배경): `#faf8ff`
- Surface Container (카드 배경): `#eaedff`
- Outline (테두리/비활성): `#767586`
- On-Surface (텍스트): `#131b2e`

타이포그래피:
- Font: Inter (sans-serif)
- 아이콘: Material Symbols Outlined, weight 400, 24px

컴포넌트:
- **희망 직군**: 탭(pill) 형식 버튼 그룹 — 선택 시 primary 배경색 적용
- **경력 수준**: 아이콘 포함 카드 3개 (school=ENTRY, work=JUNIOR, psychology=SENIOR)
- **기술 스택**: 텍스트 입력 + Enter/쉼표로 태그 추가, X 아이콘으로 개별 삭제, 추천 태그 클릭 추가
- **포트폴리오**: URL 입력 필드 + 추가 버튼, 추가된 링크 목록 표시(삭제 가능), 최대 5개
- **저장 버튼**: Primary 색상 (#4648d4), 전체 너비 또는 우측 정렬

테두리:
- sm: `0.25rem`, lg: `0.5rem`, xl: `0.75rem`, 2xl: `1rem`, full: `9999px`

텍스트:
- 페이지 타이틀: "프로필 설정"
- 섹션 레이블: "희망 직군 *", "경력 수준", "기술 스택", "포트폴리오"
- 기술 스택 플레이스홀더: "기술 스택 입력 후 Enter"
- 포트폴리오 플레이스홀더: "https://github.com/your-project"
- 추가 버튼: "추가"
- 저장 버튼: "저장 및 완료"

## 현재 구현 상태

### 이미 구현된 항목
- `front/src/shared/types/enums.ts` — `JobCategory`, `CareerLevel` enum 이미 정의됨 (추가 불필요)
- `front/src/shared/types/queryKeys.ts` — `queryKeys.profile.detail(profileId)` 이미 정의됨
- `front/src/shared/api/httpClient.ts` — axios 인스턴스, 인터셉터(silent refresh) 완성
- `front/src/shared/api/apiError.ts` — `extractApiError`, `getErrorMessage` 함수 존재 (프로필 에러 메시지 미포함)
- `front/src/shared/api/constants.ts` — `BASE_URL`, `API_PATHS` (프로필 경로 미포함)
- `front/src/shared/components/ui/Toast.tsx` — `useToast` 훅, `ToastContainer` 완성
- `front/src/shared/components/layout/AppLayout.tsx` — 사이드바 레이아웃 (프로필 설정 nav 링크 미포함)
- `front/src/features/auth/stores/authStore.ts` — `userId`, `accessToken`, `nickname` 저장
- `front/src/app/router.tsx` — 프로필 라우트 미등록

### 미구현 항목
- `front/src/features/profile/` — api, hooks, components, pages 전부 비어 있음
- 프로필 설정 사이드바 nav 링크 미추가
- 프로필 라우트(`/profile`) 미등록

## API 변경사항 (최신)

프로필 API가 다음과 같이 변경되었습니다:
- **프로필 생성**: `POST /api/users/profile` (JWT의 userId 사용, body 없음)
- **프로필 조회**: `GET /api/users/profile` (JWT의 userId 기반, path parameter 없음)
- **프로필 수정**: `PUT /api/users/profile` (body: jobCategory, careerLevel, techStacks, portfolioLinks)

모든 API가 JWT userId 기반이며 path parameter 없음.

### 구현 필요 목록
- `shared/api/constants.ts`에 프로필 API 경로 추가 (`/api/users/profile` 단일 경로)
- `shared/api/apiError.ts`에 프로필 에러 메시지 추가
- `features/profile/api/profileApi.ts` 생성 (getProfile, createProfile, updateProfile)
- `features/profile/hooks/useProfile.ts` 생성 (GET /api/users/profile)
- `features/profile/hooks/useCreateProfile.ts` 생성 (POST /api/users/profile)
- `features/profile/hooks/useUpdateProfile.ts` 생성 (PUT /api/users/profile)
- `features/profile/components/JobCategorySelector.tsx` 생성
- `features/profile/components/CareerLevelSelector.tsx` 생성
- `features/profile/components/TechStackInput.tsx` 생성
- `features/profile/components/PortfolioLinkInput.tsx` 생성
- `features/profile/pages/ProfilePage.tsx` 생성
- `shared/components/layout/AppLayout.tsx` — 프로필 설정 nav 링크 추가
- `app/router.tsx` — `/profile` 라우트 등록

## 개발 계획

### Step 1: 공통 인프라 수정

- [ ] `front/src/shared/api/constants.ts` — `API_PATHS.profile` 추가
  ```
  profile: {
    base: '/api/users/profile',
  }
  ```
- [ ] `front/src/shared/api/apiError.ts` — `ERROR_MESSAGES`에 프로필 에러 추가
  ```
  PROFILE_NOT_FOUND: '프로필을 찾을 수 없습니다.',
  PROFILE_ACCESS_DENIED: '본인의 프로필만 수정할 수 있습니다.',
  PROFILE_ALREADY_EXISTS: '이미 프로필이 존재합니다.',
  ```

### Step 2: 스킵 (authStore 수정 불필요)

- userId만으로 프로필 조회/생성 가능하므로 authStore에 profileId 저장 불필요

### Step 3: 프로필 API 함수

- [ ] `front/src/features/profile/api/profileApi.ts` 생성
  - `getProfile(): Promise<ProfileResponse>` — `GET /api/users/profile` (userId는 JWT에서 자동 추출)
  - `createProfile(): Promise<ProfileResponse>` — `POST /api/users/profile` (body 없음)
  - `updateProfile(body: UpdateProfileRequest): Promise<ProfileResponse>` — `PUT /api/users/profile`
  - 타입 정의: `ProfileResponse`, `UpdateProfileRequest` (이 파일 내부에 정의)

### Step 4: TanStack Query 훅

- [ ] `front/src/features/profile/hooks/useProfile.ts` 생성
  - `useQuery`로 `GET /api/users/profile` 호출
  - `queryKeys.profile.all` 사용
  - 프로필 없음 시 404 에러 처리
  - 에러 시 `useToast`로 메시지 표시
- [ ] `front/src/features/profile/hooks/useCreateProfile.ts` 생성
  - `useMutation`으로 `POST /api/users/profile` 호출
  - 성공 시 `queryClient.invalidateQueries({ queryKey: queryKeys.profile.all })`
  - 에러 시 `extractApiError` + `getErrorMessage`로 에러 토스트
- [ ] `front/src/features/profile/hooks/useUpdateProfile.ts` 생성
  - `useMutation`으로 `PUT /api/users/profile` 호출
  - 성공 시 `queryClient.invalidateQueries({ queryKey: queryKeys.profile.all })`
  - 성공 시 `useToast`로 "프로필이 저장되었습니다." 표시
  - 에러 시 `extractApiError` + `getErrorMessage`로 에러 토스트

### Step 5: UI 컴포넌트

- [ ] `front/src/features/profile/components/JobCategorySelector.tsx` 생성
  - Props: `value: JobCategory | null`, `onChange: (value: JobCategory) => void`
  - 탭(pill) 형식 버튼 그룹
  - 선택 상태: `background: #4648d4`, 미선택: `border: #767586`, `color: #767586`
  - 레이블: FRONTEND, BACKEND, FULLSTACK, ANDROID, IOS, DEVOPS, DATA_ENGINEER, ML_ENGINEER

- [ ] `front/src/features/profile/components/CareerLevelSelector.tsx` 생성
  - Props: `value: CareerLevel | null`, `onChange: (value: CareerLevel) => void`
  - 카드 형식 3개 (아이콘: school=ENTRY, work=JUNIOR, psychology=SENIOR)
  - 선택 상태: `border-color: #4648d4`, `background: #eaedff`
  - 아이콘: Material Symbols Outlined (CDN 또는 google-symbols npm)

- [ ] `front/src/features/profile/components/TechStackInput.tsx` 생성
  - Props: `value: string[]`, `onChange: (stacks: string[]) => void`
  - 텍스트 입력 → Enter 또는 쉼표(,)로 태그 추가
  - 태그 표시 (pill 형식) + X 버튼으로 개별 삭제
  - 추천 태그: `['Java', 'Spring Boot', 'React', 'TypeScript', 'Python', 'Node.js', 'MySQL', 'Docker']` (클릭 시 추가)
  - 최대 20개 초과 시 입력 비활성화

- [ ] `front/src/features/profile/components/PortfolioLinkInput.tsx` 생성
  - Props: `value: string[]`, `onChange: (links: string[]) => void`
  - URL 입력 필드 + "추가" 버튼
  - 추가된 링크 목록 + X 버튼으로 개별 삭제
  - 최대 5개 제한 (초과 시 "추가" 버튼 비활성화)
  - 공백 문자열 추가 방지 (트림 후 빈 문자열이면 추가 무시)

### Step 6: 프로필 페이지

- [ ] `front/src/features/profile/pages/ProfilePage.tsx` 생성
  - `useProfile()` 로 초기 데이터 로드 (userId는 JWT에서 자동 추출)
  - 프로필 없음 시 `useCreateProfile()` 호출하여 새 프로필 생성
  - `useUpdateProfile()` 로 저장 처리
  - `react-hook-form`으로 폼 상태 관리 (zod 스키마 유효성 검사)
    - `jobCategory`: required
    - `careerLevel`: required
    - `techStacks`: min 1, max 20
    - `portfolioLinks`: max 5, URL 형식
  - 로딩 중이거나 프로필이 없으면 로딩 스피너 표시
  - `useProfile` 데이터 로드 완료 시 `reset(profileData)`로 초기값 설정
  - 저장 버튼 클릭 시 `handleSubmit` → `updateProfile` 호출
  - `isPending` 시 버튼 disabled + 로딩 표시
  - 페이지 배경색: `#faf8ff`, 폼 카드 배경: `#eaedff`

### Step 7: 라우팅 및 내비게이션

- [ ] `front/src/shared/components/layout/AppLayout.tsx` — `navItems`에 `{ label: '프로필 설정', to: '/profile' }` 추가
- [ ] `front/src/app/router.tsx` — PrivateRoute + AppLayout 내부에 `/profile` 라우트 추가
  ```tsx
  <Route path="/profile" element={<ProfilePage />} />
  ```

## 파일 목록

| 파일 | 작업 | 설명 |
|------|------|------|
| `front/src/shared/api/constants.ts` | 수정 | `API_PATHS.profile` 경로 추가 |
| `front/src/shared/api/apiError.ts` | 수정 | 프로필 에러 메시지 추가 |
| `front/src/features/auth/api/authApi.ts` | 수정 | `AuthResponse`에 `profileId` 필드 추가 |
| `front/src/features/auth/stores/authStore.ts` | 수정 | `profileId` 필드 추가 및 `setAuth` 수정 |
| `front/src/features/auth/hooks/useAuthMutation.ts` | 수정 | `setAuth` 호출에 `profileId` 전달 |
| `front/src/features/profile/api/profileApi.ts` | 생성 | `getProfile`, `updateProfile` API 함수 |
| `front/src/features/profile/hooks/useProfile.ts` | 생성 | 프로필 조회 useQuery 훅 |
| `front/src/features/profile/hooks/useUpdateProfile.ts` | 생성 | 프로필 수정 useMutation 훅 |
| `front/src/features/profile/components/JobCategorySelector.tsx` | 생성 | 직군 선택 탭 컴포넌트 |
| `front/src/features/profile/components/CareerLevelSelector.tsx` | 생성 | 경력 수준 카드 컴포넌트 |
| `front/src/features/profile/components/TechStackInput.tsx` | 생성 | 기술 스택 태그 입력 컴포넌트 |
| `front/src/features/profile/components/PortfolioLinkInput.tsx` | 생성 | 포트폴리오 링크 입력 컴포넌트 |
| `front/src/features/profile/pages/ProfilePage.tsx` | 생성 | 프로필 설정 페이지 |
| `front/src/shared/components/layout/AppLayout.tsx` | 수정 | 프로필 설정 nav 링크 추가 |
| `front/src/app/router.tsx` | 수정 | `/profile` 라우트 등록 |

## 주의사항

**아키텍처 규칙**
- 모든 프로필 API는 JWT userId 기반으로 동작 — path parameter 없음
- `features/profile/` 내에서 `features/auth/`를 직접 import 금지
- `authStore`에 프로필 서버 데이터 저장 금지 — 서버 상태는 TanStack Query로만 관리
- query key는 `queryKeys.profile.all` 사용 (`shared/types/queryKeys.ts` 정의 준수)

**enum 재사용**
- `JobCategory`, `CareerLevel`은 `shared/types/enums.ts`에 이미 정의됨 → `features/profile/` 내 중복 정의 금지

**유효성 검사**
- 기술 스택: 공백 문자열 추가 방지 (trim 후 확인), 최대 20개
- 포트폴리오: 최대 5개, 공백 추가 방지
- react-hook-form + zod 사용 (`front/src/features/auth/utils/validationSchemas.ts` 패턴 참고)

**Material Icons**
- CareerLevelSelector의 아이콘(school, work, psychology)은 Google Material Symbols 사용
- CDN link 태그로 로드하거나 `@material-symbols/font-400` npm 패키지 설치 검토 (`index.html` 수정 또는 패키지 추가)

## 검증 방법

- `npm run typecheck` — TypeScript strict 오류 없음
- `npm run lint` — ESLint 오류 없음
- 수동 확인 시나리오:
  1. 로그인 후 사이드바 "프로필 설정" 링크 클릭 → `/profile` 진입 확인
  2. 기존 프로필 데이터가 폼에 자동 로드되는지 확인
  3. 직군 탭 선택 → 스타일 반영 확인
  4. 경력 수준 카드 선택 → 스타일 반영 확인
  5. 기술 스택 Enter로 태그 추가 → 태그 표시 확인, X로 삭제 확인
  6. 추천 태그 클릭 → 태그에 추가 확인
  7. 포트폴리오 URL 입력 + 추가 버튼 → 링크 목록 추가 확인, 최대 5개 제한 확인
  8. "저장 및 완료" 클릭 → 성공 토스트 표시 확인
  9. 빈 직군으로 저장 시도 → 에러 표시 확인
  10. 기술 스택 21개 추가 시도 → 입력 비활성화 확인
  11. 비로그인 상태에서 `/profile` 직접 접근 → `/login` 리다이렉트 확인
