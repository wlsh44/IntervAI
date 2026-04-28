# 아키텍처 및 코딩 규칙

## Layered Architecture (4계층)

DDD/헥사고날이 아닌 레이어드 아키텍처.

| Layer | Role | Annotation | Examples |
|-------|------|------------|----------|
| **Presentation** | 요청 수신/검증, 응답 반환 | `@RestController` | `*Controller` |
| **Business** | 비즈니스 흐름 조합 | `@Service` | `*Service`, `*FacadeService` |
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

---

## 계층별 코딩 규칙

### Presentation Layer
- Request/Response DTO는 Java `record`로 구현 (불변성)
- Bean Validation 사용 (`@Valid`, `@NotBlank`, `@NotNull` 등), validation 메시지는 한글
- Request → 도메인 Command 변환: `dto.to***()`
- Response 변환(로직 필요 시): `*Response.of(...)`
- 응답은 `ResponseEntity`로 래핑
- 비즈니스 로직을 담지 않음

### Business Layer (Service)
- `@Service` + `@RequiredArgsConstructor`
- **`@Transactional` 사용하지 않음** — 트랜잭션은 Logic Layer에서 관리
- Logic Layer 컴포넌트를 조합하여 비즈니스 흐름만 한눈에 보이도록 구현
- 조건 분기, 예외 처리 등 세부 로직은 Logic Layer로 위임

### Logic Layer
- `@Component` + `@RequiredArgsConstructor`
- `@Transactional`은 이 계층에서만 사용 (단순 읽기/단일 `save()`는 생략)
- 네이밍: `*Manager`(CUD), `*Finder`(조회), `*Reader`(읽기), `*Calculator`(계산), `*Validator`(검증), `*Handler`(복합)
- 메서드명은 간결하게: `create()`, `update()`, `delete()`, `find()`
- **Entity를 반환하지 않음** — 도메인 객체로 변환하여 반환
- 타 도메인 조회 시 Repository 직접 의존보다 해당 도메인의 Logic 클래스 우선 활용

### Data Access Layer
- Entity: `infra` 패키지, `*Entity` 네이밍, 도메인 로직 미포함
- Entity ↔ 도메인 변환: `Entity.from(domain)` (정적 팩터리), `entity.toDomain()` (인스턴스)
- Soft Delete: `EntityStatus` 기반 상태 전이, `BaseEntity` 활용
- Client: `infra` 패키지, `*Client` 네이밍

---

## 도메인 객체 규칙
- 순수 Java 객체 (JPA 어노테이션 없음)
- `@Builder`보다 생성자 직접 사용 선호
- 정적 팩터리 메서드로 생성 의도 명확화: `User.create(command, imageUrl)`

## 예외 처리
- Java 기본 예외 직접 사용 금지
- 모든 예외는 `CustomException` + `ErrorCode` enum 사용
  - 예: `throw new CustomException(ErrorCode.NOT_FOUND)`

## 인증
- `auth` 도메인 패키지에서 토큰 관련 기능 관리
- `UserInfo` 클래스에 인증된 유저 정보를 담아 Controller에 전달

## 코드 컨벤션
- wildcard import 금지 — 모든 클래스 개별 import
- 하드코딩/매직 넘버 금지 — Enum 또는 상수로 추출
- 상수는 Enum으로 구성, 가까운 패키지에 배치
- 미사용 코드(함수, 의존성 주입) 즉시 삭제
- 생성자 주입 사용 (`@RequiredArgsConstructor`), 테스트도 동일