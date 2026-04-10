# 개발 히스토리

## 2026-04-10 — Profile API 전환 + 회원가입 원자성 보장

### PR
- **[BE] feat/profile → main** : wlsh44/IntervAI#7
- **[FE] fe/feat/profile → main** : wlsh44/IntervAI#6

---

### 백엔드 변경 (feat/profile)

#### Profile API userId 기반 전환
| 변경 전 | 변경 후 |
|---------|---------|
| `GET /api/profile/{profileId}` | `GET /api/users/profile` |
| `PUT /api/profile/{profileId}` | `PUT /api/users/profile` |
| `POST` 없음 | 회원가입 시 자동 생성 (POST 엔드포인트 없음) |

- PathVariable `profileId` 제거 → JWT `userId`로 본인 프로필 식별
- `ProfileResponse`에 `updatedAt` 필드 추가

#### GET /api/users/me 추가
- JWT userId로 현재 사용자 `{ id, name }` 반환
- `UserFinder.findById()`, `UserMeResponse` 신규 추가

#### 회원가입 원자성 보장 — UserAuthHandler 도입
- 기존: `UserManager.create()` + `ProfileManager.create()` 별도 호출 (non-atomic)
- 변경: `UserAuthHandler.signUp()` — `@Transactional` 내에서 `UserRepository` + `ProfileRepository` 동시 저장
- `ProfileEntity.ofEmpty(userId)` 팩터리 메서드 추가

#### 제거된 것
- `POST /api/users/profile` (프로필 수동 생성 엔드포인트)
- `ProfileValidator`, `ProfileService.create()`
- `UserService`의 `ProfileManager` 의존성

---

### 프런트엔드 변경 (fe/feat/profile)

- `createProfile` API 함수 삭제
- `useCreateProfile` 훅 삭제
- `ProfilePage`에서 프로필 생성 로직 제거 (회원가입 시 자동 생성이므로 바로 조회)
- `ProfileResponse.updatedAt` 필드 반영 — 최근 업데이트 날짜 표시
- 포트폴리오 링크 중복 추가 방지 버그 수정
- 기술 스택 쉼표 입력 시 태그명에 쉼표 포함되는 버그 수정
