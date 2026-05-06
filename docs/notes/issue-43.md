# Issue 43 작업 로그

## 주요 결정사항

- 포트폴리오 면접 질문 생성 시 GitHub 저장소 링크를 분석해 LLM 프롬프트에 주입하도록 구현했다.
- API 요청/응답 형식은 유지했다. 기존 `portfolioLinks` 입력값을 활용해 질문 생성 단계에서 GitHub API를 호출한다.
- GitHub 분석 결과는 저장하지 않고 질문 생성 시점에만 사용한다. 저장소 설명, 기본 브랜치, 주요 언어, 언어 구성, README 일부를 프롬프트 컨텍스트로 전달한다.
- GitHub 분석 실패는 면접 질문 생성 실패로 전파하지 않고, 실패 사유를 프롬프트에 포함한다.
- 실제 LLM API 통신 검증은 `RUN_LLM_API_TESTS=true` 환경변수로 켜는 수동 테스트로 분리했다.

## 포기한 접근법

- GitHub 분석 결과를 DB에 저장하는 방식은 현재 요구보다 범위가 커서 제외했다.
- private repository 인증 연동은 사용자 토큰 관리가 필요하므로 이번 범위에서 제외했다.
- 커밋 패턴 분석은 GitHub API 호출량과 프롬프트 길이가 커질 수 있어 README/언어 구성 기반 분석을 우선 적용했다.

## 다음 작업 참고

- 수동 LLM 테스트는 아래처럼 실행한다.

```bash
API_KEY=your_llm_api_key RUN_LLM_API_TESTS=true ./gradlew test --tests "wlsh.project.intervai.question.infra.QuestionGeneratorLlmApiManualTest"
```

- `.env`를 사용할 경우 `set -a; source .env; set +a` 후 동일한 Gradle 명령을 실행하면 된다.
- GitHub API rate limit, private repository, 대용량 README 요약 정책은 이후 별도 이슈에서 고도화할 수 있다.
