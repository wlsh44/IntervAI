# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

IntervAI — AI 면접 연습 웹 애플리케이션. Claude API(SpringAI)를 활용하여 포트폴리오 기반 맞춤형 면접 질문 생성, 답변 피드백, 꼬리 질문을 제공하는 서비스.

## Tech Stack

- **Backend**: Spring Boot 3.5, Java 21, Gradle 8.14
- **LLM**: SpringAI + Anthropic Claude API (스트리밍)
- **DB**: MySQL (영구 저장), Redis (캐시/임시 저장)
- **File Storage**: AWS S3
- **Frontend** (별도): React + TypeScript + Vite, Zustand, TanStack Query, Tailwind CSS + shadcn/ui
- **Libraries**: Lombok

## Build & Run Commands

```bash
./gradlew build              # Build
./gradlew bootRun            # Run application
./gradlew test               # Run all tests
./gradlew test --tests "wlsh.project.intervai.SomeTestClass"            # Single test class
./gradlew test --tests "wlsh.project.intervai.SomeTestClass.methodName" # Single test method
./gradlew clean build        # Clean build
```

## Layered Architecture (4계층)

DDD/헥사고날이 아닌 레이어드 아키텍처. 기존 레거시 패턴을 참고하여 개발한다.

| Layer | Role | Annotation | Examples |
|-------|------|------------|----------|
| **Presentation** | 요청 수신/검증, 응답 반환 | `@RestController` | `*Controller` |
| **Business** | 비즈니스 흐름 조합 (Logic Layer 조합) | `@Service` | `*Service`, `*FacadeService` |
| **Logic** | 실제 비즈니스 로직, 데이터 접근 | `@Component` | `*Manager`, `*Finder`, `*Reader`, `*Handler`, `*Calculator`, `*Validator` |
| **Data Access** | DB/외부 API 직접 접근 | - | `*Repository`, `*Entity`, `*Client` |

### 계층 참조 규칙
- 위→아래 순방향 참조만 허용, 역류/건너뛰기 금지
- 동일 계층 간 참조 금지 (단, Logic Layer는 재사용성을 위해 서로 참조 가능)
- Service는 Repository를 직접 주입받지 않음 (Logic Layer를 통해 접근)
- 여러 Service 조합이 필요하면 `FacadeService` 사용

### 패키지 구조 (도메인별)
```
domain/         # 도메인 객체 및 Command (순수 Java, JPA 어노테이션 없음)
application/    # Service + Logic Layer (Manager, Finder, Validator 등)
infra/          # Repository, Entity, Client
presentation/   # Controller
  └─ dto/       # Request, Response DTO
```

## 계층별 코딩 규칙

### Presentation Layer
- Request/Response DTO는 Java `record`로 구현 (불변성)
- Bean Validation 사용 (`@Valid`, `@NotBlank`, `@NotNull` 등), validation 메시지는 한글
- Request → 도메인 Command 변환: `dto.to***()`
- Response 변환(로직 필요 시): `*Response.of(...)`
- 응답은 `ResponseEntity`로 래핑
- 비즈니스 로직을 담지 않음
- REST API를 가능한 따르되, 의미 전달이 우선 (단수/동사 허용)

### Business Layer (Service)
- `@Service` + `@RequiredArgsConstructor`
- **`@Transactional` 사용하지 않음** — 트랜잭션은 Logic Layer에서 관리
- Logic Layer 컴포넌트를 조합하여 비즈니스 흐름이 잘 보이도록 구현
- 외부 서비스(JwtHandler 등)를 Service에서 의존성 주입 가능

### Logic Layer
- `@Component` + `@RequiredArgsConstructor`
- `@Transactional`은 이 계층에서만 사용
  - 읽기 전용이거나 단일 `save()`는 `@Transactional` 불필요
  - 여러 Entity를 다루거나 복잡한 로직에서 사용
- 네이밍: `*Manager`(쓰기), `*Finder`(조회), `*Handler`(로직 처리), `*Reader`(읽기), `*Calculator`(계산), `*Validator`(검증)
- 메서드명은 간결하게: `create()`, `update()`, `delete()`, `find()` (불필요한 접미사 생략)
- **Entity를 반환하지 않음** — 도메인 객체로 변환하여 반환
- 타 도메인 조회 시 Repository 직접 의존보다 해당 도메인의 Logic 클래스 우선 활용

### Data Access Layer
- Entity: `infra` 패키지, `*Entity` 네이밍, 도메인 로직 미포함
- Entity ↔ 도메인 변환: `Entity.from(domain)` (정적 팩터리), `entity.toDomain()` (인스턴스)
- Soft Delete 기본: `EntityStatus` 기반 상태 전이, `BaseEntity.active/delete/isActive/isDeleted`
- Client: `infra` 패키지, `*Client` 네이밍

## 도메인 객체 규칙
- 순수 Java 객체 (JPA 어노테이션 없음)
- `@Builder`보다 생성자 직접 사용 선호
- 정적 팩터리 메서드로 생성 의도 명확화: `User.create(command, imageUrl)`

## 예외 처리
- Java 기본 예외(`IllegalArgumentException` 등) 직접 사용 금지
- 모든 예외는 `CustomException` + `ErrorCode` enum 사용
  - 예: `throw new CustomException(ErrorCode.NOT_FOUND)`

## 인증
- `auth` 패키지는 `common` 패키지에 위치 (전역 사용)
- `UserInfo` 클래스에 인증된 유저 정보를 담아 Controller에 전달

## 코드 컨벤션
- wildcard import 금지 — 모든 클래스 개별 import
- 하드코딩/매직 넘버 금지 — Enum 또는 Object로 추출
- 상수는 Enum으로 구성, 가까운 패키지에 배치
- 미사용 코드(함수, 의존성 주입) 즉시 삭제
- 생성자 주입 사용 (`@RequiredArgsConstructor`)
- 테스트도 생성자 주입

## Domain Context

- InterviewSession types: CS / PORTFOLIO / ALL
- Difficulty: ENTRY / JUNIOR / SENIOR
- Job categories: FRONTEND, BACKEND, FULLSTACK, ANDROID, IOS, DEVOPS, DATA_ENGINEER, ML_ENGINEER
- Message roles: SYSTEM / ASSISTANT / USER, types: ANSWER / FEEDBACK / QUESTION

## Development Stages

1. 사용자 인증
2. 기본 정보 및 포트폴리오 등록
3. LLM 연동 기본 채팅 (CS 질문 생성 + 스트리밍)
4. 꼬리 질문 + 실시간 피드백
5. 세션 기록 저장 및 히스토리 UI
6. 종합 리포트 + 포트폴리오 기반 질문 고도화
