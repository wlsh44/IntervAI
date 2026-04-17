# 개발 히스토리

프로젝트 진행 중 발생한 문제 상황과 해결 방법을 기록합니다.
프런트엔드 관련 히스토리는 `front/HISTORY.md`를 참조합니다.

---

## 2026-04-09 — 프로젝트 구조 개선

### 문제 1: docs 파일 역할이 혼재

**상황**: `docs/{domain}.md`와 `docs/api.md`의 역할 경계가 불명확하여 일부 도메인 파일에 API 스펙이 포함되거나 중복 내용이 존재.

**해결**: 역할을 명확히 분리.
- `docs/{domain}.md` → 비즈니스 규칙, 제약, 도메인 엔티티/구성, 진행 상황
- `docs/api.md` → 엔드포인트, Request/Response 스펙, 에러 코드만

---

### 문제 2: roadmap.md가 stale 상태로 방치

**상황**: `docs/roadmap.md`가 `project-context-manager` 에이전트 폐기 후 아무도 관리하지 않아 실제 진행 상황과 괴리 발생.

**해결**:
- `docs/roadmap.md` 삭제
- 각 `docs/{domain}.md`에 `## 진행 상황` 섹션 추가 (✅ 완료 / 🔧 진행 중 / ⬜ 예정)
- `dev-planner` 에이전트가 플랜 작성 시 해당 도메인 문서의 진행 상황을 업데이트하도록 지정

---

### 문제 3: project-context-manager 에이전트가 실제로 사용되지 않음

**상황**: 프로젝트 진행 상황 확인 용도로 만들어진 에이전트인데, 실제로 호출되지 않고 방치됨.

**원인**: 진행 상황 확인은 `docs/` 파일 읽기로 충분하며 에이전트 격리가 불필요한 작업. 콜드 스타트 비용만 발생.

**해결**: 에이전트 삭제. 진행 상황 확인은 대화에서 직접 `docs/` 파일 조회로 처리.

---

## 2026-04-10 — Profile API 전환 + 회원가입 원자성 보장

### PR
- **[BE] feat/profile → main** : wlsh44/IntervAI#7
- **[FE] fe/feat/profile → main** : wlsh44/IntervAI#6

---

### 백엔드 변경 (feat/profile)

#### Profile API userId 기반 전환
| 변경 전 | 변경 후 |
|---------|---------|
| `GET /api/profile/{profileId}` | `GET /api/users/profile` |
| `PUT /api/profile/{profileId}` | `PUT /api/users/profile` |
| `POST` 없음 | 회원가입 시 자동 생성 (POST 엔드포인트 없음) |

- PathVariable `profileId` 제거 → JWT `userId`로 본인 프로필 식별
- `ProfileResponse`에 `updatedAt` 필드 추가

#### GET /api/users/me 추가
- JWT userId로 현재 사용자 `{ id, name }` 반환
- `UserFinder.findById()`, `UserMeResponse` 신규 추가

#### 회원가입 원자성 보장 — UserAuthHandler 도입
- 기존: `UserManager.create()` + `ProfileManager.create()` 별도 호출 (non-atomic)
- 변경: `UserAuthHandler.signUp()` — `@Transactional` 내에서 `UserRepository` + `ProfileRepository` 동시 저장
- `ProfileEntity.ofEmpty(userId)` 팩터리 메서드 추가

#### 제거된 것
- `POST /api/users/profile` (프로필 수동 생성 엔드포인트)
- `ProfileValidator`, `ProfileService.create()`
- `UserService`의 `ProfileManager` 의존성

---

## 2026-04-17 — Session History 정렬 정리 + current hasNext 수정 + 테스트 Redis 충돌 해결

### 문제 1: session-history 응답이 대화 흐름이 아니라 질문/꼬리질문 묶음 순서로 내려옴

**상황**: `GET /sessions/history` 응답이 `질문1 → 질문2 → 질문3 → 꼬리질문1 → 꼬리질문2 ...` 형태로 내려와, 채팅 복원/결과 페이지에서 실제 면접 흐름과 다르게 표시됨.

**원인**:
- follow-up 질문의 `questionIndex`가 `-1`로 고정되어 있어 단순 정렬로는 대화 순서를 복원할 수 없음
- 기존 레거시 데이터에는 `parentQuestionId`가 없어 질문-꼬리질문 관계를 온전히 복원하기 어려움
- 백엔드와 프런트가 동시에 정렬을 보정하면서 책임이 중첩됨

**해결**:
- 질문 엔티티/DTO/응답에 `parentQuestionId` 추가
- 신규 follow-up 생성 시 현재 답변한 질문 ID를 부모로 저장
- 백엔드 `session-history`는 최종적으로 `q.id asc` 기준의 안정적인 생성 순서만 반환
- 대화 흐름 복원은 프런트 `orderSessionHistory()`가 단일 책임으로 수행
- `InterviewChatScreen`, 결과 페이지에서 동일한 정렬 유틸을 사용하도록 정리

---

### 문제 2: answered/unanswered 분리 정렬 과정에서 미답변 follow-up이 누락될 수 있음

**상황**: answered 부모를 가진 unanswered follow-up이 `session-history` 결과에서 빠질 수 있다는 리뷰가 제기됨.

**원인**: unanswered 집합 내부에서만 루트를 `parentQuestionId == null`로 판단하면, 부모가 answered 쪽에 있는 follow-up은 시작점이 되지 못함.

**해결**:
- 재현 테스트 추가: answered 부모를 가진 unanswered follow-up이 누락되지 않아야 함
- 이후 정렬 책임을 프런트로 일원화하면서, 백엔드는 생성 순서 반환만 담당하도록 단순화
- 리뷰 코멘트 기준으로 남은 정렬 관련 스레드 전부 반영 및 resolve

---

### 문제 3: current question API의 hasNext가 마지막 본 질문 시점에 너무 일찍 false가 됨

**상황**: 마지막 본 질문이 내려오는 순간 `hasNext=false`가 반환되어, 마지막 꼬리질문을 진행하기 전에 세션 종료처럼 보이는 문제 발생.

**원인**: `hasNext`를 `currentMainQuestionIdx < totalQuestionCount - 1`로만 계산하고 있어, 마지막 본 질문 이후 follow-up 가능성을 고려하지 못함.

**해결**:
- `QuestionFinder.findCurrent()`에 `maxFollowUpCount`를 반영
- `hasNext`를 “다음 본 질문이 남았거나, 마지막 본 질문이라도 추가 follow-up 여지가 있으면 true”로 수정
- 마지막 질문의 마지막 follow-up일 때만 `false`가 되도록 테스트 추가

---

### 문제 4: 전체 테스트 실행 시 embedded Redis 포트 충돌 발생

**상황**: 단일 테스트는 통과하지만 전체 테스트 실행 시 서로 다른 Spring 컨텍스트가 같은 Redis 포트로 embedded Redis를 띄우려 하며 `Address already in use` 예외 발생.

**원인**: 테스트용 `EmbeddedRedisConfig`가 고정 포트를 사용하고, 각 컨텍스트가 독립적으로 Redis를 시작함.

**해결**:
- 테스트 JVM 시작 시 사용 가능한 포트를 동적으로 확보
- `spring.data.redis.port`를 시스템 프로퍼티로 주입
- embedded Redis를 JVM 전체에서 한 번만 시작하고, shutdown hook에서만 종료하도록 변경
- 서로 다른 테스트 컨텍스트 조합으로 재검증하여 포트 충돌 해소 확인

---
