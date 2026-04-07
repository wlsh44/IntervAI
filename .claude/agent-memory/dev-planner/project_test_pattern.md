---
name: Controller 테스트 패턴
description: InterviewControllerTest 기반의 WebMvcTest + RestAssuredMockMvc + AcceptanceTest 패턴
type: project
---

Controller 테스트는 `AcceptanceTest`를 상속하는 `@WebMvcTest` 클래스 구조를 사용.

핵심 구성:
- `@WebMvcTest(XxxController.class)` + `extends AcceptanceTest`
- `@MockitoBean`으로 Service 주입
- `AccessTokenProvider`는 `AcceptanceTest`에서 `@MockitoBean`으로 이미 제공됨
- 인증 토큰 mock: `given(accessTokenProvider.parseUserId("valid-token")).willReturn(userId)`
- RestAssured 요청: `RestAssuredMockMvc.given().header("Authorization", "Bearer valid-token")...`

void 메서드 mock 패턴:
- `willDoNothing().given(mock).method(args)` (BDDMockito)

예외 throw mock 패턴:
- `willThrow(new CustomException(ErrorCode.XXX)).given(mock).method(args)`

인증 없는 요청 → 403, CustomException(INTERVIEW_ACCESS_DENIED) → 403, CustomException(SESSION_ALREADY_COMPLETED) → 400

**Why:** 프로젝트 전반에 동일 패턴 적용됨. 새 컨트롤러 테스트 작성 시 AcceptanceTest 상속 필수.

**How to apply:** 새 엔드포인트 테스트 작성 시 위 패턴 그대로 사용.
