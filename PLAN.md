# 개발 계획: Profile API 수정 + /api/users/me 엔드포인트 추가

## 📋 요구사항 분석

### 1. Profile API 수정 (`docs/api.md` 기준)

| API | 현재 | 목표 |
|-----|------|------|
| 프로필 생성 | 없음 (회원가입 시 자동 생성만) | `POST /api/users/profile` (body 없음, JWT userId 사용) |
| 프로필 조회 | `GET /api/profile/{profileId}` | `GET /api/users/profile` (pathVariable 없음, JWT userId 사용) |
| 프로필 수정 | `PUT /api/profile/{profileId}` | `PUT /api/users/profile` (pathVariable 없음, JWT userId 사용) |

- ProfileResponse에 `updatedAt` 필드 추가 필요 (ISO 8601 형식)
- 조회/수정 API는 `profileId` 대신 JWT의 `userId`로 본인 프로필 식별

### 2. `/api/users/me` 엔드포인트 추가

- `GET /api/users/me` — JWT userId로 사용자 정보 반환
- 응답: `{ id: Long, name: String }` (닉네임을 name 필드로 반환)
- 인증 필요

---

## 🔍 현재 구현 상태

### ProfileController (`profile/presentation/ProfileController.java`)
- 경로: `/api/profile` (목표: `/api/users/profile`)
- `PUT /api/profile/{profileId}` — profileId PathVariable 사용
- `GET /api/profile/{profileId}` — profileId PathVariable 사용
- `POST /api/users/profile` (프로필 생성 엔드포인트) 자체가 없음
  - 프로필 생성은 회원가입(`UserService.create()`) 시 `ProfileManager.create()`로 자동 생성 중

### ProfileService (`profile/application/ProfileService.java`)
- `updateProfile(Long userId, Long profileId, UpdateProfileCommand)` — profileId 파라미터 존재
- `getProfile(Long userId, Long profileId)` — profileId 파라미터 존재
- 프로필 생성 메서드 없음 (UserService에서 ProfileManager 직접 호출)

### ProfileValidator (`profile/application/ProfileValidator.java`)
- `validateProfileOwner(Long profileId, Long userId)` — profileId 기반 검증

### ProfileFinder (`profile/application/ProfileFinder.java`)
- `findById(Long profileId)` 메서드만 존재
- userId 기반 조회 메서드 없음

### ProfileManager (`profile/application/ProfileManager.java`)
- `create(Long userId, CreateProfileCommand)` 존재
- `update(Long profileId, UpdateProfileCommand)` 존재 — profileId 기반

### Profile 도메인 (`profile/domain/Profile.java`)
- `updatedAt` 필드 없음

### ProfileEntity (`profile/infra/ProfileEntity.java`)
- `BaseEntity` 상속 — `modifiedAt` 필드 이미 존재 (JPA Auditing)
- `toDomain(techStacks, portfolioLinks)` 메서드가 `updatedAt`을 전달하지 않음

### UserController (`user/presentation/UserController.java`)
- `/api/users/sign-up`, `/api/users/login` 존재
- `GET /api/users/me` 없음

### UserFinder (`user/application/UserFinder.java`)
- `findByNickname(String nickname)` 메서드만 존재
- `findById(Long userId)` 없음

### UserRepository (`user/infra/UserRepository.java`)
- `findByIdAndStatus` 없음

### ProfileRepository (`profile/infra/ProfileRepository.java`)
- `findByUserId` 관련 메서드 없음 — userId 기반 조회 불가

---

## ⚠️ 차이점 및 버그

### 누락된 기능
1. `POST /api/users/profile` 엔드포인트 없음
2. `GET /api/users/me` 엔드포인트 없음
3. ProfileResponse에 `updatedAt` 필드 없음
4. `ProfileFinder.findByUserId()` 없음
5. `ProfileManager.updateByUserId()` 없음
6. `UserFinder.findById(Long userId)` 없음
7. `USER_NOT_FOUND` ErrorCode 없음

### 버그 및 아키텍처 위반
8. ProfileController 경로 불일치: `/api/profile` → `/api/users/profile`
9. ProfileController PathVariable profileId 사용: userId 기반으로 전환 필요
10. ProfileService 시그니처 변경 필요: profileId 파라미터 제거
11. ProfileValidator `validateProfileOwner()`: userId 기반 전환 후 불필요해짐 → 삭제 대상
12. Profile 도메인에 `updatedAt` 필드 추가 필요 → `toDomain()` 변환 체인 전체 수정 필요
13. 기존 테스트 전면 수정 필요

---

## 🗺️ 개발 계획

### Step 1: ErrorCode에 USER_NOT_FOUND 추가

- [ ] `ErrorCode.java` — `USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")` 추가

### Step 2: Profile 도메인 객체에 updatedAt 추가

- [ ] `Profile.java` — `updatedAt` 필드(`LocalDateTime`) 추가, 생성자 및 `of()` 팩터리 메서드 시그니처 수정
- [ ] `ProfileEntity.java` — `toDomain(List<String> techStacks, List<String> portfolioLinks)` → `toDomain(List<String> techStacks, List<String> portfolioLinks, LocalDateTime updatedAt)` 시그니처 변경 후 내부에서 `modifiedAt` 전달

### Step 3: ProfileRepository에 userId 기반 조회 추가

- [ ] `ProfileRepository.java` — `findByUserIdAndStatus(Long userId, EntityStatus status)` 메서드 추가

### Step 4: ProfileFinder에 userId 기반 조회 추가

- [ ] `ProfileFinder.java` — `findByUserId(Long userId)` 메서드 추가
  - `ProfileRepository.findByUserIdAndStatus()` 호출, 없으면 `PROFILE_NOT_FOUND` 예외
  - 기존 `findById()` 내부 로직(TechStack, PortfolioLink 조합) 동일하게 적용
  - `toDomain()` 호출 시 `modifiedAt` 추가 전달

### Step 5: ProfileManager 수정

- [ ] `ProfileManager.java`:
  - `create()` 메서드의 `toDomain()` 호출 시 `modifiedAt` 전달 방식 수정 (신규 생성 시 null 또는 현재 시각)
  - `updateByUserId(Long userId, UpdateProfileCommand command)` 메서드 추가
    - `ProfileRepository.findByUserIdAndStatus()` 호출
    - 기존 `update()` 내부 로직(techStack/portfolioLink soft delete 후 재저장) 재활용

### Step 6: ProfileValidator 수정

- [ ] `ProfileValidator.java`:
  - `validateProfileNotExists(Long userId)` 추가 — 이미 존재하면 `PROFILE_ALREADY_EXISTS` 예외
  - 기존 `validateProfileOwner(Long profileId, Long userId)` 삭제 (userId 기반 전환으로 불필요)

### Step 7: ProfileService 전면 수정

- [ ] `ProfileService.java`:
  - `create(Long userId)` 추가 — `profileValidator.validateProfileNotExists(userId)` → `profileManager.create(userId, EMPTY_PROFILE)` 호출, Profile 반환
  - `getProfile(Long userId)` — profileId 파라미터 제거, `profileFinder.findByUserId(userId)` 호출
  - `updateProfile(Long userId, UpdateProfileCommand)` — profileId 파라미터 제거, `profileManager.updateByUserId(userId, command)` 호출

### Step 8: ProfileController 전면 수정

- [ ] `ProfileController.java`:
  - 경로: `/api/profile` → `/api/users/profile`
  - `POST /api/users/profile` 추가 — body 없음, `profileService.create(userId)` 호출, 201 반환
  - `GET /api/users/profile` — PathVariable 제거, `profileService.getProfile(userId)` 호출
  - `PUT /api/users/profile` — PathVariable 제거, `profileService.updateProfile(userId, command)` 호출

### Step 9: ProfileResponse에 updatedAt 추가

- [ ] `ProfileResponse.java` — `updatedAt` 필드(`LocalDateTime`) 추가
  - `of(Profile profile)` 정적 팩터리 수정

### Step 10: UserRepository에 findByIdAndStatus 추가

- [ ] `UserRepository.java` — `findByIdAndStatus(Long id, EntityStatus status)` 메서드 추가

### Step 11: UserFinder에 findById 추가

- [ ] `UserFinder.java` — `findById(Long userId)` 메서드 추가
  - `UserRepository.findByIdAndStatus(userId, EntityStatus.ACTIVE)` 호출
  - 없으면 `CustomException(ErrorCode.USER_NOT_FOUND)` 예외

### Step 12: UserMeResponse DTO 생성

- [ ] `user/presentation/dto/UserMeResponse.java` — `record UserMeResponse(Long id, String name)` 생성
  - `of(User user)` 정적 팩터리 메서드 포함 (`user.getNickname()`을 `name`에 매핑)

### Step 13: UserService에 getMe() 추가

- [ ] `UserService.java` — `getMe(Long userId)` 메서드 추가
  - `userFinder.findById(userId)` 호출 후 `User` 반환

### Step 14: UserController에 /me 엔드포인트 추가

- [ ] `UserController.java` — `GET /api/users/me` 추가
  - `@AuthenticationPrincipal UserInfo userInfo`로 userId 추출
  - `userService.getMe(userId)` 호출
  - `UserMeResponse.of(user)` 반환, 200 OK

### Step 15: 테스트 수정 및 추가

- [ ] `ProfileControllerTest.java` 전면 수정:
  - 경로: `/api/profile/{profileId}` → `/api/users/profile`
  - `profileService.updateProfile(userId, profileId, command)` → `profileService.updateProfile(userId, command)` mock 수정
  - `profileService.getProfile(userId, profileId)` → `profileService.getProfile(userId)` mock 수정
  - 응답에 `updatedAt` 필드 검증 추가 (null이어도 필드 존재 확인 또는 not null 확인)
  - `POST /api/users/profile` 생성 테스트 추가 (성공 201, 중복 409)

- [ ] `ProfileServiceTest.java` 수정:
  - `getProfile(userId, profileId)` → `getProfile(userId)` 형태로 수정
  - `updateProfile(userId, profileId, command)` → `updateProfile(userId, command)` 형태로 수정
  - `create(userId)` 테스트 추가 (성공, 중복 생성 실패)

- [ ] `ProfileValidatorTest.java` — `validateProfileOwner` 테스트 삭제, `validateProfileNotExists` 테스트 추가

- [ ] `UserControllerTest.java` (기존 또는 신규) — `GET /api/users/me` 테스트 추가:
  - 성공: 200 + `{ id, name }` 검증
  - 인증 없이 요청: 403

---

## 📁 영향받는 파일

### 수정 필요
- `src/main/java/wlsh/project/intervai/common/exception/ErrorCode.java`
- `src/main/java/wlsh/project/intervai/profile/domain/Profile.java`
- `src/main/java/wlsh/project/intervai/profile/infra/ProfileEntity.java`
- `src/main/java/wlsh/project/intervai/profile/infra/ProfileRepository.java`
- `src/main/java/wlsh/project/intervai/profile/application/ProfileFinder.java`
- `src/main/java/wlsh/project/intervai/profile/application/ProfileManager.java`
- `src/main/java/wlsh/project/intervai/profile/application/ProfileService.java`
- `src/main/java/wlsh/project/intervai/profile/application/ProfileValidator.java`
- `src/main/java/wlsh/project/intervai/profile/presentation/ProfileController.java`
- `src/main/java/wlsh/project/intervai/profile/presentation/dto/ProfileResponse.java`
- `src/main/java/wlsh/project/intervai/user/infra/UserRepository.java`
- `src/main/java/wlsh/project/intervai/user/application/UserFinder.java`
- `src/main/java/wlsh/project/intervai/user/application/UserService.java`
- `src/main/java/wlsh/project/intervai/user/presentation/UserController.java`
- `src/test/java/wlsh/project/intervai/profile/presentation/ProfileControllerTest.java`
- `src/test/java/wlsh/project/intervai/profile/application/ProfileServiceTest.java`
- `src/test/java/wlsh/project/intervai/profile/application/ProfileValidatorTest.java`

### 생성 필요
- `src/main/java/wlsh/project/intervai/user/presentation/dto/UserMeResponse.java`

---

## ⚡ 주의사항

1. **Service에서 Entity 사용 금지**: `ProfileService`, `UserService`에서 Entity를 직접 참조하지 않음. Logic Layer를 통해서만 접근.

2. **updatedAt 전달 경로 전체 수정**: `ProfileEntity.modifiedAt` (BaseEntity) → `ProfileEntity.toDomain()` → `Profile.updatedAt` → `ProfileResponse.updatedAt`. `toDomain()` 호출 지점이 `ProfileFinder`와 `ProfileManager` 두 곳이므로 모두 수정 필요.

3. **ProfileManager.create()의 updatedAt 처리**: 신규 생성 직후에는 `modifiedAt`이 JPA Auditing에 의해 설정되나, `profileRepository.save()` 직후 반환된 `ProfileEntity`에서 `getModifiedAt()`으로 가져올 수 있음. null 반환 가능성에 유의.

4. **UserService의 ProfileManager 의존 유지**: 회원가입 시 자동 프로필 생성(`profileManager.create()` 직접 호출)은 그대로 유지. `ProfileService.create()`는 수동 생성 API 전용.

5. **ProfileValidator.validateProfileOwner() 삭제**: userId 기반 조회 자체가 소유권 보장이므로 별도 검증 불필요. 삭제 후 참조 지점(`ProfileService`) 반드시 정리.

6. **SecurityConfig 변경 불필요**: `/api/users/profile`, `/api/users/me` 모두 `.anyRequest().authenticated()` 규칙으로 커버됨.

7. **ProfileResponse.updatedAt 타입**: `LocalDateTime` 사용. Jackson이 ISO 8601 직렬화하도록 `@JsonFormat` 필요 여부 확인 (기존 프로젝트 설정 확인).

---

## 🧪 테스트 명령

```bash
# Profile 관련 전체 테스트
./gradlew test --tests "wlsh.project.intervai.profile.*"

# User 관련 전체 테스트
./gradlew test --tests "wlsh.project.intervai.user.*"

# 특정 테스트 클래스
./gradlew test --tests "wlsh.project.intervai.profile.presentation.ProfileControllerTest"
./gradlew test --tests "wlsh.project.intervai.profile.application.ProfileServiceTest"
./gradlew test --tests "wlsh.project.intervai.profile.application.ProfileValidatorTest"

# 전체 테스트
./gradlew test
```
