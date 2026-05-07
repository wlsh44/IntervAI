# IntervAI

IntervAI는 개발자 면접 연습을 위한 AI 기반 서비스의 백엔드 애플리케이션입니다.  
사용자의 기본 정보, 기술 스택, 포트폴리오 링크를 바탕으로 면접 세션을 만들고, AI 질문 생성, 답변 피드백, 꼬리 질문, 면접 리포트 생성을 담당합니다.

![이미지 관련 아키텍처]()

## 주요 기능

- 회원가입, 로그인, JWT 기반 인증
- 직군, 경력, 기술 스택, 포트폴리오 링크 관리
- CS/프로젝트 기반 면접 세션 생성
- AI 질문 생성 및 답변 제출
- 답변별 피드백과 점수 제공
- 세션 히스토리 조회 및 면접 결과 리포트

## 기술 스택

- Java 21
- Spring Boot 3.5
- Spring Security, Spring Data JPA, Validation
- Spring AI
- MySQL, Redis, H2(local/test)
- Gradle

## 프로젝트 구조

```text
.
├── src/main/java/wlsh/project/intervai
│   ├── common       # 공통 설정, 인증, 예외 처리
│   ├── user         # 사용자 계정
│   ├── profile      # 프로필, 기술 스택, 포트폴리오 링크
│   ├── interview    # 면접 생성 및 진행
│   ├── question     # 질문 생성
│   ├── answer       # 답변 제출 및 결과 생성
│   ├── feedback     # 피드백 저장/조회
│   ├── session      # 면접 세션 히스토리
│   └── report       # 면접 리포트
└── docs             # 기능 명세 및 API 문서
```

> 다이어그램 추가 추천: 백엔드 도메인 간 관계가 확정되면 이 위치에 간단한 아키텍처 다이어그램을 추가하면 좋습니다.

## 로컬 실행

```bash
./gradlew bootRun
```

기본 실행 프로파일은 `local`입니다. 로컬 개발에서는 필요에 따라 `local-seed`, `local-ollama`, `local-gemini` 등 리소스 프로파일을 선택해 실행합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local,local-seed'
```

## 테스트

```bash
./gradlew test
```

## 문서

- [문서 인덱스](docs/README.md)
- [API 문서](docs/api.md)
- [아키텍처 및 코딩 규칙](docs/architecture.md)

> 시퀀스 다이어그램 추가 추천: 면접 세션 생성 → 질문 생성 → 답변 제출 → 피드백/꼬리 질문 생성 흐름은 `docs/api.md` 또는 README 하단에 Mermaid 시퀀스 다이어그램으로 정리하기 좋습니다.
