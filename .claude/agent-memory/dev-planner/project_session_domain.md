---
name: InterviewSession 도메인 구현 현황
description: session 패키지의 Logic Layer 클래스들과 역할 분담 패턴
type: project
---

InterviewSession 도메인의 Logic Layer 구성:

- `InterviewSessionFinder`: sessionId 또는 interviewId로 Entity/도메인 조회. `getEntity()`, `find()`, `getEntityByInterviewId()`, `findByInterviewId()` 제공. SESSION_NOT_FOUND 예외 처리 담당.
- `InterviewSessionManager`: CUD 담당. `create()`, `advanceToNext()`, `addFollowUp()` 구현됨. `complete()` 미구현 상태였음 (2026-04-07 기준 PLAN.md에서 추가 예정).
- `InterviewSessionValidator`: 소유자 검증 담당. `validateInterviewOwner()` 구현됨. `validateSessionInProgress()` 미구현 상태였음.

Entity 주요 메서드:
- `complete()`: sessionStatus=COMPLETED, completedAt=now() 설정 — 이미 구현됨
- `isInProgress()`: sessionStatus == IN_PROGRESS 여부 반환 — 이미 구현됨
- `isOwner(userId)`: userId 일치 여부 반환

**Why:** session 도메인은 Finder/Manager/Validator 역할 분리가 명확하게 구현되어 있음. 새 기능 추가 시 이 패턴 준수 필요.

**How to apply:** finish 같은 상태 변경 기능은 Manager에 추가, 검증은 Validator에 추가, 조회는 Finder 활용.
