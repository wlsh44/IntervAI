---
name: intervai-be
description: Use for IntervAI backend work in Spring Boot/Java, including domain logic, APIs, persistence, prompts, reports, profiles, auth, tests, or backend docs.
---

# IntervAI Backend Skill

Use this skill when changing backend code or backend-facing contracts.

## Scope

- Backend code under `src/main/java` and `src/test/java`
- Prompt resources under `src/main/resources/prompts`
- Backend configuration, Gradle setup, and persistence mappings
- API contracts in `docs/api.md` when backend request or response shapes change

## Project Context

- Stack: Java 21, Spring Boot 3.5, Gradle, SpringAI, MySQL, Redis, S3.
- Local application config files matching `src/main/resources/application*.yml` are intentionally ignored and should not be committed.
- Prompt generation is part of backend behavior. When prompt builders are touched, preserve debug-level prompt logging and avoid leaking prompts at info level.
- Local seed data belongs behind the local profile path and must not activate in normal runtime.

## Backend Architecture Rules

- Follow the existing package boundaries before adding new structure.
- Keep controller code thin. Request validation and response mapping should match nearby controllers.
- Services should orchestrate use cases. Prefer existing Finder, Manager, Validator, Factory, and Repository patterns rather than reaching across layers ad hoc.
- Keep domain mutation behavior close to the domain or manager classes already responsible for it.
- Use transaction boundaries consistent with neighboring code.
- Avoid changing frontend files unless an API contract changed and the frontend must compile against the new contract.

## Implementation Order

`Domain → Service → Repository(Entity) → Controller → 단위 테스트`

Reason: domain and service logic must be stable before the controller exposes them. Do not skip ahead.

## Commit Units

Split commits by layer or logical unit. Do not mix layers in one commit.

- `feat: add {Domain} domain/entity`
- `feat: add {Domain}Service`
- `feat: add {Domain}Repository`
- `feat: add {Domain}Controller {method} {path}`
- `test: add {Domain}Service unit tests`

Convention: `feat / fix / refactor / test / docs / chore`. No WIP or temp messages.

## Compact Checkpoints

Run `/compact` at these points to prevent hallucination from context bloat:

- Before starting implementation (after Plan is approved)
- When switching layers (e.g., Service → Controller)
- After 3+ consecutive test fix cycles
- When context usage exceeds 50%

**Before each compact, verify:**
- [ ] `docs/api.md` reflects current state
- [ ] All completed work is committed and pushed
- [ ] Incomplete work has TODO comments

## Hard Stop Conditions

Stop immediately and report to user if any of the following occur:

- A file outside the original plan needs editing
- Estimated commits exceed 5
- The same test failure recurs with 3 different fixes attempted → stop + re-enter Plan Mode
- Implementation reveals the initial design needs structural change → stop + report (do not redesign alone)

## Workflow

1. Read `docs/api.md`, `docs/architecture.md`, and relevant domain docs under `docs/`.
2. Locate existing code with `rg` before introducing new classes or helpers.
3. Make the smallest coherent backend change that fits the current design.
4. Follow Implementation Order above.
5. Add or update focused tests for behavioral changes.
6. Run a targeted Gradle test when possible, then `./gradlew test` for shared behavior or broad changes.
7. If an API shape changed, update `docs/api.md` in the same change (user approval required).
8. On completion, hand off to `intervai-ship` skill.

## Checks

```bash
./gradlew test
./gradlew test --tests "wlsh.project.intervai.SomeTestClass"
./gradlew test --tests "wlsh.project.intervai.SomeTestClass.methodName"
```
