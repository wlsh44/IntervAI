# GET /api/interviews 백엔드 구현 계획

## 📋 요구사항 분석

`docs/api.md` 및 `docs/session-history.md` 기반.

- `GET /api/interviews` — 인증된 사용자의 면접 목록을 페이지네이션으로 반환
- Query Parameters: `page` (기본값 0), `size` (기본값 10)
- 응답 필드: `content[]`, `totalElements`, `totalPages`, `last`
- `InterviewSummary` 항목: `id`, `interviewType`, `difficulty`, `questionCount`, `sessionStatus`, `createdAt`
- 날짜 내림차순 정렬 (createdAt DESC)
- 면접이 없으면 빈 목록 반환 (에러 없음)
- `sessionStatus`는 연관된 `InterviewSession`의 상태(`IN_PROGRESS` / `COMPLETED`)

---

## 🔍 현재 구현 상태

### 구현 완료
- `InterviewEntity` — `userId`, `interviewType`, `difficulty`, `questionCount`, `createdAt` 필드 존재 (`BaseEntity`에서 `createdAt` 상속)
- `InterviewRepository` — `findByIdAndStatus()` 단건 조회만 존재
- `InterviewSessionEntity` — `interviewId`, `sessionStatus` 필드 존재
- `InterviewSessionRepository` — `findByInterviewIdAndStatus()` 존재
- `InterviewFinder` — 단건 조회(`find(Long interviewId)`)만 존재
- `InterviewService` — `create()` 메서드만 존재
- `InterviewController` — `POST /api/interviews` 만 존재 (`@GetMapping` 없음)

### 미구현
- `InterviewRepository`에 userId 기반 페이지네이션 쿼리 없음
- `InterviewFinder`에 목록 조회 메서드 없음
- `InterviewService`에 목록 조회 메서드 없음
- `InterviewController`에 `GET /api/interviews` 엔드포인트 없음
- `InterviewSummary` 도메인 객체 없음
- `InterviewListResponse`, `InterviewSummaryResponse` DTO 없음

---

## ⚠️ 차이점 및 버그

### 아키텍처 위반 (수정 필요)
1. **`InterviewSessionValidator`가 `InterviewRepository`를 직접 주입받음**
   - 파일: `session/application/InterviewSessionValidator.java`
   - 위반: Logic Layer는 타 도메인 조회 시 해당 도메인의 Logic 클래스를 우선 활용해야 함 (`architecture.md` 규칙)
   - 수정: `InterviewRepository` 의존 제거 → `InterviewFinder`로 교체

### 누락된 기능
2. **`InterviewRepository`에 페이지네이션 쿼리 없음**
   - `findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, EntityStatus status, Pageable pageable)` 필요

3. **`sessionStatus`를 포함한 목록 조회**
   - `Interview`와 `InterviewSession`을 JOIN하거나, 별도 쿼리로 조합하는 전략 필요
   - `InterviewSession`은 `interviewId`로 조회 가능하나, 목록 조회 시 N+1 문제 방지를 위해 `interviewId` IN 절로 일괄 조회 후 조합하는 방식 채택

4. **`InterviewSummary` 도메인 객체 없음**
   - `interview/domain/InterviewSummary.java` 생성 필요

5. **`InterviewListResponse`, `InterviewSummaryResponse` DTO 없음**
   - `interview/presentation/dto/InterviewListResponse.java`
   - `interview/presentation/dto/InterviewSummaryResponse.java`

---

## 🗺️ 개발 계획

### Step 1: 아키텍처 위반 수정 — `InterviewSessionValidator` 리팩터링
- [ ] `session/application/InterviewSessionValidator.java` 수정
  - `InterviewRepository` 의존 제거
  - `InterviewFinder`를 주입받아 `validateInterviewOwner()` 구현

### Step 2: Data Access Layer 확장
- [ ] `interview/infra/InterviewRepository.java` — 페이지네이션 쿼리 추가
  ```java
  Page<InterviewEntity> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, EntityStatus status, Pageable pageable);
  ```
- [ ] `session/infra/InterviewSessionRepository.java` — IN 절 일괄 조회 추가
  ```java
  List<InterviewSessionEntity> findByInterviewIdInAndStatus(List<Long> interviewIds, EntityStatus status);
  ```

### Step 3: 도메인 객체 생성 — `InterviewSummary`
- [ ] `interview/domain/InterviewSummary.java` 생성
  - 필드: `id`, `interviewType`, `difficulty`, `questionCount`, `sessionStatus`, `createdAt`
  - `InterviewSessionStatus`를 `session/domain`에서 참조 (단방향 의존 허용: interview → session 방향 아님, 따라서 공통 enum으로 처리하거나 `interview/domain`에 별도 enum 정의)
  - **주의**: `interview` 도메인이 `session` 도메인 패키지를 직접 참조하는 것은 도메인 간 결합 발생. `sessionStatus` 타입은 `String` 또는 `interview/domain/SessionStatus.java` enum으로 독립 정의 권장
  - `InterviewSummary.of(InterviewEntity entity, String sessionStatus, LocalDateTime createdAt)` 정적 팩터리 메서드

### Step 4: Logic Layer 확장 — `InterviewFinder`
- [ ] `interview/application/InterviewFinder.java` 수정
  - `findSummaries(Long userId, Pageable pageable)` 메서드 추가
  - `InterviewRepository`로 페이지 조회 → `interviewId` 목록 추출 → `InterviewSessionRepository`로 세션 일괄 조회 (N+1 방지) → `InterviewSummary` 목록 조합 반환
  - `InterviewSessionRepository`를 직접 의존하거나, `InterviewSessionFinder`에 일괄 조회 메서드 추가 후 활용
  - **설계 결정**: `InterviewFinder`는 `interview` 패키지 소속이므로 `session` 패키지 Logic 클래스 직접 참조는 순환/교차 의존 우려. `InterviewSessionFinder`에 `findByInterviewIds(List<Long> ids)` 추가 후 `InterviewFinder`에서 참조하는 방식 채택

### Step 5: Business Layer — `InterviewService` 확장
- [ ] `interview/application/InterviewService.java` 수정
  - `getList(Long userId, Pageable pageable)` 메서드 추가
  - `interviewFinder.findSummaries(userId, pageable)` 호출 후 반환

### Step 6: Presentation Layer — DTO 및 Controller 확장
- [ ] `interview/presentation/dto/InterviewSummaryResponse.java` 생성 (record)
  - 필드: `id`, `interviewType`, `difficulty`, `questionCount`, `sessionStatus`, `createdAt`
  - `InterviewSummaryResponse.of(InterviewSummary)` 정적 팩터리

- [ ] `interview/presentation/dto/InterviewListResponse.java` 생성 (record)
  - 필드: `content`, `totalElements`, `totalPages`, `last`
  - `InterviewListResponse.of(Page<InterviewSummary>)` 정적 팩터리

- [ ] `interview/presentation/InterviewController.java` 수정
  - `@GetMapping` 추가
  - `@RequestParam(defaultValue = "0") int page`, `@RequestParam(defaultValue = "10") int size` 파라미터
  - `PageRequest.of(page, size)` 생성 → `interviewService.getList()` 호출 → `InterviewListResponse.of()` 반환

### Step 7: 테스트 작성
- [ ] `interview/presentation/InterviewControllerTest.java` 수정
  - `GET /api/interviews` 성공 케이스 (목록 있는 경우)
  - 빈 목록 반환 케이스
  - 인증 없이 요청 시 403 케이스

---

## 📁 영향받는 파일

### 수정
| 파일 | 변경 내용 |
|------|----------|
| `session/application/InterviewSessionValidator.java` | `InterviewRepository` → `InterviewFinder` 교체 |
| `interview/infra/InterviewRepository.java` | 페이지네이션 쿼리 메서드 추가 |
| `session/infra/InterviewSessionRepository.java` | IN 절 일괄 조회 메서드 추가 |
| `session/application/InterviewSessionFinder.java` | `findByInterviewIds(List<Long>)` 메서드 추가 |
| `interview/application/InterviewFinder.java` | `findSummaries(Long userId, Pageable pageable)` 메서드 추가 |
| `interview/application/InterviewService.java` | `getList(Long userId, Pageable pageable)` 메서드 추가 |
| `interview/presentation/InterviewController.java` | `GET /api/interviews` 엔드포인트 추가 |
| `interview/presentation/InterviewControllerTest.java` | 목록 조회 테스트 케이스 추가 |

### 생성
| 파일 | 설명 |
|------|------|
| `interview/domain/InterviewSummary.java` | 목록 조회용 도메인 객체 |
| `interview/domain/SessionStatus.java` | interview 도메인 내 세션 상태 enum (IN_PROGRESS / COMPLETED) — session 도메인과 중복이지만 도메인 간 결합 방지를 위해 독립 정의 |
| `interview/presentation/dto/InterviewSummaryResponse.java` | 목록 항목 DTO |
| `interview/presentation/dto/InterviewListResponse.java` | 페이지네이션 응답 DTO |

---

## ⚡ 주의사항

1. **도메인 간 enum 중복 허용**: `session/domain/InterviewSessionStatus`와 `interview/domain/SessionStatus`는 같은 값이지만 별도 정의. interview 도메인이 session 도메인 패키지를 직접 참조하면 도메인 간 결합이 발생하므로 interview 패키지에 독립 enum을 두는 것이 올바름.

2. **N+1 방지**: `findSummaries()`에서 `InterviewSession`을 interview 건수만큼 개별 조회하면 N+1 발생. `findByInterviewIdInAndStatus()`로 일괄 조회 후 `Map<Long, InterviewSessionEntity>`로 변환하여 조합.

3. **Session 없는 Interview 처리**: 면접 설정 생성(`POST /api/interviews`) 후 세션 생성 전 상태의 Interview가 존재할 수 있음. `sessionStatus`가 null일 경우 처리 방식 결정 필요 (예: `IN_PROGRESS`로 기본값 처리 또는 null 반환).

4. **`InterviewSessionValidator` 수정 시 기존 테스트 영향 없음**: `InterviewFinder`가 동일한 예외를 던지므로 동작 변경 없음.

5. **`@Transactional` 금지**: `InterviewService.getList()`에 `@Transactional` 사용 금지 (Service 계층 규칙). `InterviewFinder.findSummaries()`에서 필요하면 `@Transactional(readOnly = true)` 사용 가능.

6. **createdAt 형식**: 응답의 `createdAt`은 ISO 8601 형식(`LocalDateTime` → Jackson 직렬화). `BaseEntity.createdAt`이 `LocalDateTime` 타입이므로 별도 포맷 설정 필요 여부 확인.

---

## 🧪 테스트 명령

```bash
# 전체 테스트
./gradlew test

# Interview 관련 테스트만
./gradlew test --tests "wlsh.project.intervai.interview.*"

# Controller 테스트만
./gradlew test --tests "wlsh.project.intervai.interview.presentation.InterviewControllerTest"
```
