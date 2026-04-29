# 히스토리 기능 백엔드 구현 계획 (Issue #21)

## 📋 요구사항 분석

docs/api.md 및 Issue #21 기반으로 파악한 요구사항:

1. **GET /api/interviews 필터 확장**
   - 현재: `page`, `size` 파라미터만 지원
   - 추가: `interviewType` (선택, `CS` / `PORTFOLIO` / `ALL`), `sessionStatus` (선택, `IN_PROGRESS` / `COMPLETED`)
   - 필터 미지정 시 전체 조회 (기존 동작 유지)

2. **DELETE /api/interviews/{interviewId} (soft delete)**
   - 본인 면접만 삭제 가능 (403: `INTERVIEW_ACCESS_DENIED`)
   - 204 No Content 반환
   - 연관 엔티티 soft delete: `InterviewSession`, `Question`, `Answer`, `Feedback`, `InterviewCsSubject`, `InterviewPortfolioLink`
   - `BaseEntity.delete()`를 활용한 status → `DELETED` 전이

3. **docs/api.md 업데이트**
   - GET /api/interviews 파라미터 섹션에 필터 파라미터 추가
   - DELETE /api/interviews/{interviewId} 엔드포인트 추가

---

## 🔍 현재 구현 상태

### 이미 구현된 것들

- `InterviewController.getList()`: page/size 기반 페이지네이션 조회 동작
- `InterviewFinder.findSummaries()`: `InterviewRepository.findByUserIdAndStatusOrderByCreatedAtDesc()` 호출, sessionMap 조립
- `InterviewRepository`: `findByUserIdAndStatusOrderByCreatedAtDesc()` 메서드 존재
- `InterviewSessionValidator.validateInterviewOwner()`: interviewId + userId로 소유자 검증 (interview_access_denied 처리 포함)
- `BaseEntity.delete()`: status → DELETED soft delete 메서드 기구현
- `ErrorCode.INTERVIEW_ACCESS_DENIED` (403): 이미 정의됨
- 연관 엔티티 Repository: `InterviewSessionRepository`, `QuestionRepository`, `AnswerRepository`, `FeedbackRepository`, `InterviewCsSubjectRepository`, `InterviewPortfolioLinkRepository` 모두 존재

### 아키텍처 특이사항

- `InterviewSessionValidator`가 `InterviewRepository`와 `InterviewSessionRepository`를 직접 주입받음 — 이는 기존 동작 패턴이므로 유지
- `InterviewSummary` (domain 객체)가 `InterviewEntity`를 import하고 있음 → **아키텍처 위반** (domain 계층이 infra 계층 참조)

---

## ⚠️ 차이점 및 버그

### 누락된 기능

1. **필터 파라미터 미지원**: `GET /api/interviews`에 `interviewType`, `sessionStatus` 파라미터 없음
2. **DELETE 엔드포인트 없음**: `DELETE /api/interviews/{interviewId}` 미구현
3. **연쇄 soft delete 로직 없음**: Interview 삭제 시 연관 엔티티 처리 로직 전무

### 불완전한 구현

4. **필터용 Repository 쿼리 없음**: `InterviewRepository`에 `interviewType`, `sessionStatus` 필터를 지원하는 쿼리 메서드가 없음
5. **필터용 세션 상태 조인/서브쿼리 없음**: sessionStatus는 `interview_sessions` 테이블에 있어 `interviews` 테이블 단독 조회로는 필터 불가 — 쿼리 전략 설계 필요
6. **QuestionRepository에 interviewId 기반 일괄 조회 없음**: 삭제 시 `findByInterviewIdAndStatus()` 불필요 메서드 추가 필요
7. **AnswerRepository에 interviewId 기반 일괄 조회 없음**: `findByInterviewIdAndStatus()` 추가 필요
8. **FeedbackRepository에 questionId 복수 기반 일괄 조회는 있으나**, answerId 목록 기반으로 연쇄 삭제해야 하는 흐름 검토 필요

### 아키텍처 위반

9. **`InterviewSummary.of(InterviewEntity, SessionStatus)` — domain이 infra 참조**: `InterviewSummary`가 `InterviewEntity`를 직접 import하고 있음. 이번 작업 범위에서 수정 가능하나 기존 동작에 영향을 주므로 별도 Step으로 분리

---

## 🗺️ 개발 계획

### Step 1: 필터 파라미터용 Repository 쿼리 추가

**목적**: interviewType + sessionStatus 필터를 DB 수준에서 처리

- [ ] `InterviewRepository`에 JPQL 쿼리 메서드 추가
  - 전략: `InterviewSession`을 JOIN하여 sessionStatus 필터 처리 (N+1 방지)
  - 메서드명: `findSummaries(Long userId, InterviewType interviewType, SessionStatus sessionStatus, Pageable pageable)`
  - 파일: `src/main/java/wlsh/project/intervai/interview/infra/InterviewRepository.java`
  - 구체적 쿼리:
    ```sql
    SELECT i FROM InterviewEntity i
    JOIN InterviewSessionEntity s ON s.interviewId = i.id AND s.status = 'ACTIVE'
    WHERE i.userId = :userId
      AND i.status = 'ACTIVE'
      AND (:interviewType IS NULL OR i.interviewType = :interviewType)
      AND (:sessionStatus IS NULL OR s.sessionStatus = :sessionStatus)
    ORDER BY i.createdAt DESC
    ```
  - 주의: `@Query` JPQL 사용, nullable 파라미터는 `@Param`으로 전달
  - 반환 타입: `Page<Object[]>` 또는 전용 Projection 인터페이스 — Projection 권장 (InterviewSummaryProjection)

- [ ] `InterviewSummaryProjection` 인터페이스 생성 (infra 패키지)
  - 파일: `src/main/java/wlsh/project/intervai/interview/infra/InterviewSummaryProjection.java`
  - 필드: `getId()`, `getInterviewType()`, `getDifficulty()`, `getQuestionCount()`, `getCreatedAt()`, `getSessionStatus()`
  - 참고: sessionStatus는 `InterviewSessionEntity`의 컬럼이므로 JOIN 결과에서 가져와야 함

### Step 2: InterviewFinder.findSummaries() 시그니처 변경

**목적**: 필터 파라미터를 받아서 처리

- [ ] `InterviewFinder.findSummaries()` 메서드 시그니처 변경
  - 파일: `src/main/java/wlsh/project/intervai/interview/application/InterviewFinder.java`
  - 변경 전: `findSummaries(Long userId, Pageable pageable)`
  - 변경 후: `findSummaries(Long userId, InterviewType interviewType, SessionStatus sessionStatus, Pageable pageable)`
  - 내부 로직: Step 1에서 추가한 Repository 쿼리 사용. Projection 결과를 `InterviewSummary` 도메인 객체로 변환
  - 기존의 sessionMap 조립 로직 제거 (JOIN 쿼리로 대체)

- [ ] `InterviewSummary.of()` 정적 팩터리 수정
  - 파일: `src/main/java/wlsh/project/intervai/interview/domain/InterviewSummary.java`
  - `InterviewEntity` import 제거 (아키텍처 위반 수정)
  - `InterviewSummaryProjection` 파라미터를 받는 팩터리 메서드로 교체 또는 필드 직접 받는 기존 of() 활용

### Step 3: InterviewService.getList() 시그니처 변경

**목적**: 필터를 Service 계층으로 전달

- [ ] `InterviewService.getList()` 메서드 시그니처 변경
  - 파일: `src/main/java/wlsh/project/intervai/interview/application/InterviewService.java`
  - 변경 전: `getList(Long userId, Pageable pageable)`
  - 변경 후: `getList(Long userId, InterviewType interviewType, SessionStatus sessionStatus, Pageable pageable)`

### Step 4: InterviewController.getList() 파라미터 추가

**목적**: 클라이언트 요청의 필터 파라미터 수신

- [ ] `InterviewController.getList()` 수정
  - 파일: `src/main/java/wlsh/project/intervai/interview/presentation/InterviewController.java`
  - `@RequestParam(required = false) InterviewType interviewType` 추가
  - `@RequestParam(required = false) SessionStatus sessionStatus` 추가
  - Service 호출 시 파라미터 전달

### Step 5: 삭제용 Repository 메서드 추가

**목적**: Interview soft delete 시 연관 엔티티 일괄 조회/삭제에 필요한 쿼리 추가

- [ ] `QuestionRepository`에 메서드 추가
  - 파일: `src/main/java/wlsh/project/intervai/question/infra/QuestionRepository.java`
  - `List<QuestionEntity> findByInterviewIdAndStatus(Long interviewId, EntityStatus status)`

- [ ] `AnswerRepository`에 메서드 추가
  - 파일: `src/main/java/wlsh/project/intervai/answer/infra/AnswerRepository.java`
  - `List<AnswerEntity> findByInterviewIdAndStatus(Long interviewId, EntityStatus status)`

- [ ] `FeedbackRepository` 기존 메서드 확인
  - `findByAnswerIdInAndStatus(List<Long>, EntityStatus)` 이미 존재 → 재사용 가능

- [ ] `InterviewCsSubjectRepository`에 메서드 추가
  - 파일: `src/main/java/wlsh/project/intervai/interview/infra/InterviewCsSubjectRepository.java`
  - `List<InterviewCsSubjectEntity> findByInterviewIdAndStatus(Long interviewId, EntityStatus status)` — `findByInterviewIdAndStatusOrderByIdAsc()`가 이미 있으므로 재사용 가능 (ORDER BY 무관)

- [ ] `InterviewPortfolioLinkRepository` 기존 메서드 확인
  - `findByInterviewIdAndStatusOrderByIdAsc()` 이미 존재 → 재사용 가능

- [ ] `InterviewSessionRepository` 기존 메서드 확인
  - `findByInterviewIdAndStatus()` 이미 존재 → 재사용 가능

### Step 6: InterviewManager에 delete() 메서드 추가

**목적**: Interview + 연관 엔티티 일괄 soft delete 트랜잭션 처리

- [ ] `InterviewManager.delete()` 메서드 추가
  - 파일: `src/main/java/wlsh/project/intervai/interview/application/InterviewManager.java`
  - `@Transactional`로 원자성 보장
  - 삭제 순서 (의존성 고려): Feedback → Answer → Question → Session → CsSubject → PortfolioLink → Interview
  - 각 연관 Repository에서 ACTIVE 상태 엔티티를 조회 후 `entity.delete()` 호출
  - 현재 `InterviewManager`는 `InterviewCsSubjectRepository`, `InterviewPortfolioLinkRepository`, `InterviewRepository`를 주입받고 있음
  - 추가 주입 필요: `QuestionRepository`, `AnswerRepository`, `FeedbackRepository`, `InterviewSessionRepository` (또는 각 도메인의 Finder 활용)

  > 아키텍처 검토: `InterviewManager`(interview 도메인 Logic Layer)가 `QuestionRepository`, `AnswerRepository`, `FeedbackRepository`를 직접 의존하는 것은 타 도메인 Repository 직접 의존 문제. `architecture.md`에서 "타 도메인 조회 시 해당 도메인의 Logic 클래스 우선 활용" 명시. 따라서 각 도메인의 Finder/Manager를 통해 삭제 위임하는 방식 선택.

  - 위임 방식:
    - `QuestionRepository.findByInterviewIdAndStatus()` → 직접 삭제 불가 → `QuestionManager.deleteByInterviewId(Long interviewId)` 위임
    - `AnswerRepository.findByInterviewIdAndStatus()` → `AnswerManager.deleteByInterviewId(Long interviewId)` 위임
    - 단, 각 Manager가 없으면 이번 작업에서 생성
    - Feedback은 Answer 삭제와 연동 — `AnswerManager.deleteByInterviewId()` 내부에서 연쇄 처리

- [ ] `QuestionManager` 생성 또는 `deleteByInterviewId()` 메서드 추가
  - 파일: `src/main/java/wlsh/project/intervai/question/application/QuestionManager.java` (없으면 신규 생성)
  - `@Transactional`
  - `QuestionRepository.findByInterviewIdAndStatus()` 후 각 entity.delete() 호출

- [ ] `AnswerManager` 생성 또는 `deleteByInterviewId()` 메서드 추가
  - 파일: `src/main/java/wlsh/project/intervai/answer/application/AnswerManager.java` (없으면 신규 생성)
  - `@Transactional`
  - AnswerEntity 목록 조회 → answerId 목록 추출 → `FeedbackRepository.findByAnswerIdInAndStatus()` 로 Feedback 삭제 → Answer 삭제
  - Feedback 삭제 시 `FeedbackManager.deleteByAnswerIds()` 위임 또는 직접 처리 결정 필요 (AnswerManager 내부에서 처리가 더 단순)

  > 설계 결정: FeedbackManager가 없으면, AnswerManager가 FeedbackRepository를 직접 주입받아 처리. 이는 동일 "삭제 트랜잭션" 맥락이므로 허용 가능.

- [ ] `InterviewManager.delete(Long interviewId, Long userId)` 메서드 추가
  - 흐름: 소유자 검증 없이 (이미 Service에서 처리) → QuestionManager.deleteByInterviewId() → AnswerManager.deleteByInterviewId() → InterviewSession soft delete → CsSubject/PortfolioLink soft delete → InterviewEntity soft delete
  - InterviewSession 삭제: `InterviewSessionRepository`를 직접 주입 또는 `InterviewSessionManager.delete()` 위임
  - 선택: InterviewManager가 InterviewSessionRepository를 직접 주입 (단순하고 interview 도메인 내에 session 삭제 책임 부여)

  > 최종 삭제 순서: QuestionManager → AnswerManager(+Feedback) → InterviewSession → CsSubject → PortfolioLink → Interview

### Step 7: InterviewService에 delete() 추가

**목적**: Service 계층에서 소유자 검증 후 Manager 위임

- [ ] `InterviewService.delete()` 메서드 추가
  - 파일: `src/main/java/wlsh/project/intervai/interview/application/InterviewService.java`
  - `InterviewValidator`를 활용한 소유자 검증 (또는 `InterviewSessionValidator.validateInterviewOwner()` 재사용)
  - 실제로는 `InterviewFinder.find()`로 Interview 조회 → userId 비교 → `InterviewManager.delete(interviewId)` 호출

  > 소유자 검증 설계: 현재 `InterviewSessionValidator.validateInterviewOwner()`가 InterviewRepository를 직접 사용. interview 도메인 서비스에서는 `InterviewFinder.find()`로 Interview 도메인 객체를 가져온 뒤 `interview.getUserId().equals(userId)` 비교 + throw가 더 clean. 또는 `InterviewValidator`에 `validateOwner(Long interviewId, Long userId)` 메서드 추가.

- [ ] `InterviewValidator.validateOwner(Long interviewId, Long userId)` 추가
  - 파일: `src/main/java/wlsh/project/intervai/interview/application/InterviewValidator.java`
  - `InterviewFinder`를 주입받아 `find(interviewId)` 후 userId 비교
  - 단, InterviewValidator가 InterviewFinder를 주입받으면 동일 계층 간 순환 의존 문제 없는지 확인 (둘 다 Logic Layer이므로 허용)

### Step 8: InterviewController에 delete() 엔드포인트 추가

**목적**: DELETE 엔드포인트 노출

- [ ] `InterviewController.delete()` 추가
  - 파일: `src/main/java/wlsh/project/intervai/interview/presentation/InterviewController.java`
  - `@DeleteMapping("/{interviewId}")`
  - `@AuthenticationPrincipal UserInfo userInfo`, `@PathVariable Long interviewId`
  - `interviewService.delete(userInfo.userId(), interviewId)` 호출
  - `ResponseEntity.noContent().build()` (204) 반환
  - `@DeleteMapping` import 추가 필요

### Step 9: docs/api.md 업데이트

- [ ] `GET /api/interviews` 섹션의 Query Parameters 테이블에 필터 파라미터 추가
  - `interviewType`: InterviewType, 선택, 필터 없으면 전체 조회
  - `sessionStatus`: SessionStatus, 선택, 필터 없으면 전체 조회

- [ ] `DELETE /api/interviews/{interviewId}` 엔드포인트 섹션 추가
  - 인증: 필요
  - Path Parameters: interviewId
  - Response: 204 No Content
  - 에러: INTERVIEW_NOT_FOUND(404), INTERVIEW_ACCESS_DENIED(403)

- [ ] 에러 코드 레퍼런스 테이블에 신규 추가 사항 반영 (이미 두 코드 모두 존재하므로 추가 불필요)

### Step 10: 테스트 작성

- [ ] `InterviewControllerTest` 작성
  - 파일: `src/test/java/wlsh/project/intervai/interview/InterviewControllerTest.java`
  - `@WebMvcTest(InterviewController.class)` + `extends AcceptanceTest`
  - `@MockitoBean InterviewService interviewService`

  테스트 케이스:
  - `getList_필터없이_전체조회_성공()`: interviewType/sessionStatus 미지정 → 200
  - `getList_interviewType필터_적용_성공()`: `?interviewType=CS` → 200
  - `getList_sessionStatus필터_적용_성공()`: `?sessionStatus=COMPLETED` → 200
  - `getList_두필터_동시적용_성공()`: `?interviewType=CS&sessionStatus=COMPLETED` → 200
  - `getList_인증없이_접근_실패()`: Authorization 헤더 없음 → 403
  - `delete_성공()`: DELETE /{interviewId} → 204
  - `delete_타인면접_접근실패()`: `INTERVIEW_ACCESS_DENIED` throw → 403
  - `delete_없는면접_실패()`: `INTERVIEW_NOT_FOUND` throw → 404
  - `delete_인증없이_접근_실패()`: Authorization 헤더 없음 → 403

---

## 📁 영향받는 파일

### 수정 파일

| 파일 | 변경 내용 |
|------|-----------|
| `interview/presentation/InterviewController.java` | getList() 파라미터 추가, delete() 엔드포인트 추가 |
| `interview/application/InterviewService.java` | getList() 시그니처 변경, delete() 추가 |
| `interview/application/InterviewFinder.java` | findSummaries() 시그니처 변경, Projection 기반으로 로직 교체 |
| `interview/application/InterviewValidator.java` | validateOwner() 추가 |
| `interview/application/InterviewManager.java` | delete() 추가, QuestionManager/AnswerManager 주입 추가 |
| `interview/domain/InterviewSummary.java` | InterviewEntity import 제거 (아키텍처 위반 수정), Projection 기반 of() 추가 또는 교체 |
| `interview/infra/InterviewRepository.java` | 필터 + JOIN 쿼리 메서드 추가 |
| `interview/infra/InterviewCsSubjectRepository.java` | 기존 메서드 재사용 (수정 불필요) |
| `interview/infra/InterviewPortfolioLinkRepository.java` | 기존 메서드 재사용 (수정 불필요) |
| `question/infra/QuestionRepository.java` | findByInterviewIdAndStatus() 추가 |
| `answer/infra/AnswerRepository.java` | findByInterviewIdAndStatus() 추가 |
| `docs/api.md` | GET /api/interviews 파라미터 추가, DELETE 엔드포인트 추가 |

### 신규 생성 파일

| 파일 | 내용 |
|------|------|
| `interview/infra/InterviewSummaryProjection.java` | 필터 쿼리 결과 Projection 인터페이스 |
| `question/application/QuestionManager.java` | deleteByInterviewId() (없는 경우) |
| `answer/application/AnswerManager.java` | deleteByInterviewId() with Feedback 연쇄 삭제 (없는 경우) |
| `src/test/java/.../interview/InterviewControllerTest.java` | Controller 단위 테스트 |

> QuestionManager, AnswerManager 파일이 이미 존재한다면 해당 메서드만 추가

---

## ⚡ 주의사항

### 아키텍처 규칙

1. **Service에서 Entity 직접 사용 금지**: `InterviewService.delete()`는 `InterviewFinder`로 도메인 객체를 가져온 후 userId 비교. Entity를 직접 다루지 않음.

2. **Logic Layer 간 참조**: `InterviewManager`에서 `QuestionManager`, `AnswerManager`를 주입받는 것은 Logic Layer 간 재사용 허용 규칙에 따라 정상.

3. **@Transactional 위치**: `InterviewManager.delete()`에 `@Transactional` 부여. Service에는 `@Transactional` 금지.

4. **InterviewSummary domain 오염 수정**: `InterviewSummary.of(InterviewEntity, ...)` 팩터리는 domain이 infra를 참조하는 위반. `InterviewSummaryProjection` 기반으로 교체하거나, `of(Long, InterviewType, ...)` 기존 오버로드를 사용하도록 변경.

5. **필터 쿼리의 NULL 처리**: JPQL에서 `(:interviewType IS NULL OR i.interviewType = :interviewType)` 패턴 사용 시 Hibernate에서 enum 타입 null 비교가 예상대로 동작하지 않을 수 있음. `@Query` + `@Param` + `Optional` 파라미터 방식 검토 필요. 대안으로 Spring Data JPA Specification 패턴 또는 QueryDSL 사용 고려. **Specification 도입 여부는 구현 전 팀 협의 권장.**

6. **삭제 연쇄 범위**: `InterviewSessionRepository.findByInterviewIdAndStatus()`는 단건 반환(`Optional`). 삭제 시에도 단건 처리 충분 (인터뷰당 세션은 1개).

7. **QuestionRepository.findByInterviewIdAndStatus()**: 현재 해당 메서드 없음. 이번 작업에서 추가 필요.

### 구현 순서 주의

- Step 1(Repository 쿼리) → Step 2(Finder) → Step 3(Service) → Step 4(Controller) 순서로 하향식 구현
- 삭제 기능은 Step 5(Repository) → Step 6(Manager) → Step 7(Service) → Step 8(Controller) 순서
- 테스트는 최종 단계에서 일괄 작성 가능

---

## 🧪 테스트 명령

```bash
# 전체 테스트
./gradlew test

# interview 도메인 테스트만 실행
./gradlew test --tests "wlsh.project.intervai.interview.*"

# 신규 Controller 테스트 단독 실행
./gradlew test --tests "wlsh.project.intervai.interview.InterviewControllerTest"
```
