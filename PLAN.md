# 면접 세션 종료 구현 계획

## 목표

`POST /api/interviews/{interviewId}/finish` 엔드포인트를 구현하여 면접 세션 상태를 COMPLETED로 변경한다.

검증 순서:
1. 본인 면접인지 확인 (INTERVIEW_ACCESS_DENIED)
2. 세션 존재 확인 (SESSION_NOT_FOUND)
3. 이미 완료된 세션인지 확인 (SESSION_ALREADY_COMPLETED)
4. 세션 상태를 COMPLETED로 변경, completedAt 저장

응답: 200 OK (body 없음)

---

## 변경/추가 파일 목록

| 구분 | 파일 경로 |
|------|-----------|
| 수정 | `src/main/java/wlsh/project/intervai/session/application/InterviewSessionManager.java` |
| 수정 | `src/main/java/wlsh/project/intervai/session/application/InterviewSessionValidator.java` |
| 수정 | `src/main/java/wlsh/project/intervai/session/application/InterviewSessionService.java` |
| 수정 | `src/main/java/wlsh/project/intervai/interview/presentation/InterviewController.java` |
| 수정 | `src/test/java/wlsh/project/intervai/interview/presentation/InterviewControllerTest.java` |

---

## 구현 단계

### Step 1: `InterviewSessionManager` — complete() 메서드 추가

- 파일 경로: `src/main/java/wlsh/project/intervai/session/application/InterviewSessionManager.java`
- 변경 내용:
  - `complete(Long interviewId)` 메서드 추가
  - `InterviewSessionFinder`를 주입받아 `getEntityByInterviewId(interviewId)`로 Entity 조회
  - 조회한 Entity의 `complete()` 호출
  - `@Transactional` 적용 (Entity 상태 변경 후 flush 보장)
  - 반환값 없음 (`void`)
- 주의사항:
  - `InterviewSessionFinder`는 이미 `SESSION_NOT_FOUND` 예외를 처리하므로 Manager에서 중복 처리 불필요
  - `isInProgress()` 검증은 `InterviewSessionValidator`에서 담당하므로 Manager에서 수행하지 않음
  - 조회 방식은 `findByIdAndStatus(ACTIVE)` 기반인 `getEntityByInterviewId()`를 그대로 활용 — IN_PROGRESS 상태 세션은 ACTIVE BaseEntity status를 가지므로 정상 조회됨

### Step 2: `InterviewSessionValidator` — validateSessionInProgress() 메서드 추가

- 파일 경로: `src/main/java/wlsh/project/intervai/session/application/InterviewSessionValidator.java`
- 변경 내용:
  - `validateSessionInProgress(Long interviewId)` 메서드 추가
  - `InterviewSessionFinder`를 주입받아 `getEntityByInterviewId(interviewId)`로 Entity 조회
  - `entity.isInProgress()`가 false이면 `throw new CustomException(ErrorCode.SESSION_ALREADY_COMPLETED)` 발생
- 주의사항:
  - `InterviewSessionFinder`를 Validator에 주입하는 것은 Logic Layer 간 참조이므로 허용됨
  - 현재 `InterviewSessionValidator`는 `InterviewRepository`만 주입받고 있으므로 `InterviewSessionFinder` 필드 추가 필요
  - `SESSION_NOT_FOUND`와 `SESSION_ALREADY_COMPLETED`는 별도 예외로 구분 — 조회 실패 시 Finder가 먼저 NOT_FOUND를 던지고, 조회 성공 후 isInProgress()가 false일 때 ALREADY_COMPLETED를 던지는 흐름

### Step 3: `InterviewSessionService` — finish() 메서드 추가

- 파일 경로: `src/main/java/wlsh/project/intervai/session/application/InterviewSessionService.java`
- 변경 내용:
  - `finish(Long userId, Long interviewId)` 메서드 추가
  - 비즈니스 흐름 순서대로 아래 세 호출을 나열:
    1. `interviewSessionValidator.validateInterviewOwner(interviewId, userId)` — 소유자 검증
    2. `interviewSessionValidator.validateSessionInProgress(interviewId)` — 완료 여부 검증
    3. `interviewSessionManager.complete(interviewId)` — 상태 변경
  - 반환값 없음 (`void`)
- 주의사항:
  - Service에 `@Transactional` 추가 금지 — 트랜잭션은 Step 1의 Manager에서 관리
  - 조건 분기나 예외 처리를 Service에 직접 작성하지 않음 — Logic Layer로 위임된 형태 유지
  - `SESSION_NOT_FOUND`는 Step 2의 Validator에서 Finder를 통해 암묵적으로 처리됨 — 별도 조회 불필요

### Step 4: `InterviewController` — finish() 엔드포인트 추가

- 파일 경로: `src/main/java/wlsh/project/intervai/interview/presentation/InterviewController.java`
- 변경 내용:
  - `finish(@AuthenticationPrincipal UserInfo userInfo, @PathVariable Long interviewId)` 메서드 추가
  - `@PostMapping("/{interviewId}/finish")` 매핑
  - `interviewSessionService.finish(userInfo.userId(), interviewId)` 호출
  - `ResponseEntity.ok().build()` 반환 (200 OK, body 없음)
- 주의사항:
  - Request body 없음 — `@RequestBody` 불필요
  - `@Valid` 불필요

### Step 5: `InterviewControllerTest` — finish 관련 테스트 추가

- 파일 경로: `src/test/java/wlsh/project/intervai/interview/presentation/InterviewControllerTest.java`
- 변경 내용:
  - 아래 4개 테스트 메서드 추가:

  **1. `finishSession` — 성공 시 200 반환**
  - `given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId)`
  - `willDoNothing().given(interviewSessionService).finish(userId, interviewId)` (void 메서드 mock)
  - `POST /api/interviews/{interviewId}/finish` 요청
  - 응답 상태코드 200 검증, body 없음 확인

  **2. `finishSessionWithoutAuth` — 인증 없이 요청 시 403 반환**
  - Authorization 헤더 없이 `POST /api/interviews/1/finish` 요청
  - 응답 상태코드 403 검증

  **3. `finishSessionWhenNotOwner` — 타인의 세션 종료 시도 시 403 반환**
  - `interviewSessionService.finish()`가 `CustomException(ErrorCode.INTERVIEW_ACCESS_DENIED)` 던지도록 mock
  - 응답 상태코드 403 검증

  **4. `finishSessionWhenAlreadyCompleted` — 이미 완료된 세션 종료 시도 시 400 반환**
  - `interviewSessionService.finish()`가 `CustomException(ErrorCode.SESSION_ALREADY_COMPLETED)` 던지도록 mock
  - 응답 상태코드 400 검증

- 주의사항:
  - 기존 테스트 패턴 (`given(accessTokenProvider.parseUserId(...))`) 동일하게 사용
  - `willDoNothing()` 패턴: `willDoNothing().given(mock).method(args)` (BDDMockito)
  - `willThrow()` 패턴: `willThrow(new CustomException(ErrorCode.XXX)).given(mock).method(args)`

---

## 검증 방법

### 단위 테스트
- `InterviewControllerTest`의 신규 4개 테스트 통과 확인
  ```
  ./gradlew test --tests "wlsh.project.intervai.interview.presentation.InterviewControllerTest"
  ```

### 전체 테스트
- 기존 테스트 회귀 없음 확인
  ```
  ./gradlew test
  ```

### 수동 검증 시나리오 (필요 시)
1. 정상 종료: 본인 세션에 `POST /api/interviews/{interviewId}/finish` → 200
2. 타인 세션: 다른 userId로 동일 요청 → 403
3. 존재하지 않는 세션: 없는 interviewId로 요청 → 404
4. 중복 종료: 이미 완료된 세션에 재요청 → 400
