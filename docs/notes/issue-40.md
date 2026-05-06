# Issue 40 작업 로그

## 주요 결정사항

- `GET /api/health`는 인증 없이 호출 가능한 운영 확인용 API로 추가했다.
- Healthcheck는 비즈니스 로직이 없으므로 별도 Service 계층 없이 얇은 Controller와 응답 record만 두었다.
- 프런트 운영 API 주소는 `front/.env.production`에 `VITE_API_BASE_URL=https://intervai.kro.kr`로 분리했다.
- 운영 인스턴스에서는 GitHub Actions가 publish한 이미지를 pull해서 실행하므로 `compose.yml`의 로컬 `build` 설정을 제거했다.

## 포기한 접근법

- Spring Boot Actuator 도입은 현재 요구 범위보다 크고 의존성/노출 경로 관리가 추가로 필요해 사용하지 않았다.
- 프런트 API 기본값 자체를 운영 도메인으로 바꾸는 방식은 로컬 개발 기본 동작을 깨뜨릴 수 있어 사용하지 않았다.

## 다음 작업 참고

- `GET /api/health` 응답은 현재 `{"status":"UP"}`만 반환한다.
- 운영 서버 `.env`에는 프런트 CORS 허용 도메인과 백엔드 런타임 환경변수가 별도로 유지되어야 한다.
- #40은 백엔드 PR과 프런트엔드 PR을 분리했으며, API 문서 스펙 변경은 선행 커밋으로 `main`에 반영했다.
