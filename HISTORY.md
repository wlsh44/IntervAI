# 개발 히스토리

프로젝트 진행 중 발생한 문제 상황과 해결 방법을 기록합니다.
프런트엔드 관련 히스토리는 `front/HISTORY.md`를 참조합니다.

---

## 2026-04-09 — 프로젝트 구조 개선

### 문제 1: docs 파일 역할이 혼재

**상황**: `docs/{domain}.md`와 `docs/api.md`의 역할 경계가 불명확하여 일부 도메인 파일에 API 스펙이 포함되거나 중복 내용이 존재.

**해결**: 역할을 명확히 분리.
- `docs/{domain}.md` → 비즈니스 규칙, 제약, 도메인 엔티티/구성, 진행 상황
- `docs/api.md` → 엔드포인트, Request/Response 스펙, 에러 코드만

---

### 문제 2: roadmap.md가 stale 상태로 방치

**상황**: `docs/roadmap.md`가 `project-context-manager` 에이전트 폐기 후 아무도 관리하지 않아 실제 진행 상황과 괴리 발생.

**해결**:
- `docs/roadmap.md` 삭제
- 각 `docs/{domain}.md`에 `## 진행 상황` 섹션 추가 (✅ 완료 / 🔧 진행 중 / ⬜ 예정)
- `dev-planner` 에이전트가 플랜 작성 시 해당 도메인 문서의 진행 상황을 업데이트하도록 지정

---

### 문제 3: project-context-manager 에이전트가 실제로 사용되지 않음

**상황**: 프로젝트 진행 상황 확인 용도로 만들어진 에이전트인데, 실제로 호출되지 않고 방치됨.

**원인**: 진행 상황 확인은 `docs/` 파일 읽기로 충분하며 에이전트 격리가 불필요한 작업. 콜드 스타트 비용만 발생.

**해결**: 에이전트 삭제. 진행 상황 확인은 대화에서 직접 `docs/` 파일 조회로 처리.

---

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
