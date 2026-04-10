---
name: 공통 에러 유틸 패턴
description: 에러 처리 시 extractApiError + ERROR_MESSAGES + getErrorMessage 조합 사용
type: reference
---

`front/src/shared/api/apiError.ts`에 세 가지 유틸이 있다.

- `extractApiError(error: unknown): ApiError` — axios 에러에서 `{ code, message }` 추출
- `ERROR_MESSAGES: Record<string, string>` — 에러 코드 → 한국어 메시지 매핑 테이블
- `getErrorMessage(code: string): string` — 코드로 메시지 조회 (없으면 fallback 반환)

새 도메인 에러 코드는 반드시 `ERROR_MESSAGES`에 추가한 뒤 hook의 onError에서 `getErrorMessage(code)`로 토스트를 띄운다.

현재 등록된 코드: 인증 관련(INVALID_TOKEN, EXPIRED_TOKEN, LOGIN_FAILED, DUPLICATE_NICKNAME), 면접 관련(INTERVIEW_NOT_FOUND, INTERVIEW_ACCESS_DENIED 등), UNKNOWN_ERROR.

프로필 에러(PROFILE_NOT_FOUND, PROFILE_ACCESS_DENIED)는 아직 미등록 상태.
