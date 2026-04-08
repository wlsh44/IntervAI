# 사용자 인증

## 개요
- 목적: 사용자 회원가입, 로그인, 토큰 기반 세션 관리를 통해 안전한 서비스 접근을 보장한다.
- 대상 사용자: 서비스에 처음 가입하거나 기존 계정으로 로그인하는 모든 사용자
- 관련 개발 단계: Stage 1 (완료)

## 요구사항
### 기능 요구사항
- [x] 닉네임 / 비밀번호 기반 회원가입
- [x] 닉네임 중복 검증
- [x] 비밀번호 BCrypt 암호화 저장
- [x] 닉네임 / 비밀번호 기반 로그인
- [x] JWT Access Token 발급 (유효기간 24시간)
- [x] JWT Refresh Token 발급 (유효기간 7일, Redis 저장)
- [x] Refresh Token을 HttpOnly 쿠키로 관리
- [x] Refresh Token을 이용한 Access Token 갱신 (Refresh Token Rotation)
- [x] Bearer 토큰 파싱 및 SecurityContext에 UserInfo 설정

### 비기능 요구사항
- [x] Refresh Token은 Redis에 저장하여 만료 처리 및 무효화를 용이하게 한다.
- [x] Refresh Token은 HttpOnly 쿠키로 전달하여 XSS 공격으로부터 보호한다.
- [x] 비밀번호는 최소 길이 등 강도 정책을 적용한다. (4자 이상 12자 이하, `INVALID_PASSWORD_LENGTH`)

## 사용자 스토리
- As a 신규 사용자, I want to 닉네임과 비밀번호로 회원가입을 하고 싶다 so that 서비스를 이용할 수 있다.
- As a 기존 사용자, I want to 닉네임과 비밀번호로 로그인하고 싶다 so that 내 계정으로 서비스를 이용할 수 있다.
- As a 로그인된 사용자, I want to Access Token이 만료되었을 때 자동으로 갱신되고 싶다 so that 다시 로그인하는 불편함 없이 서비스를 이어 이용할 수 있다.

## 사용자 플로우
1. 사용자가 닉네임과 비밀번호를 입력하여 회원가입 요청을 보낸다.
2. 서버는 닉네임 중복을 검증하고 비밀번호를 BCrypt로 암호화하여 저장한다.
3. 사용자가 닉네임과 비밀번호로 로그인 요청을 보낸다.
4. 서버는 자격증명을 검증하고 Access Token과 Refresh Token을 발급한다.
5. Access Token은 응답 바디로, Refresh Token은 HttpOnly 쿠키로 전달된다.
6. 클라이언트는 API 요청 시 Authorization 헤더에 Bearer Access Token을 포함한다.
7. Access Token 만료 시 클라이언트는 Refresh Token 쿠키를 이용해 갱신을 요청한다.
8. 서버는 Refresh Token을 검증하고 새로운 Access Token과 Refresh Token을 발급한다 (Rotation).

## 수용 기준 (Acceptance Criteria)
- [x] `POST /api/users/sign-up` — 닉네임 중복 시 에러 반환, 성공 시 사용자 생성
- [x] `POST /api/users/login` — 잘못된 자격증명 시 에러 반환, 성공 시 토큰 쌍 반환
- [x] `POST /api/auth/refresh` — 유효한 Refresh Token 쿠키로 새 Access Token 발급
- [x] 인증 필요 API에서 Access Token 없거나 유효하지 않으면 401 반환
- [x] 닉네임 중복 시 적절한 에러코드(`CustomException` + `ErrorCode`)로 응답

## 범위 외 (Out of Scope)
- 소셜 로그인 (Google, GitHub OAuth)
- 이메일 인증
- 비밀번호 찾기 / 재설정
- 계정 탈퇴

## 미결 사항 (Open Questions)
- Access Token 만료 시 클라이언트 자동 갱신 처리 방식 (인터셉터 구현 여부)

## 관련 도메인/엔티티
- `User` — id, nickname (unique), passwordHash
- `TokenPair` — accessToken, refreshToken
- `UserInfo` — 인증된 사용자 정보 (SecurityContext 보관)
- Redis — Refresh Token 저장소
