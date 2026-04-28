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

## Workflow

1. Read the relevant docs first: `docs/api.md`, `docs/architecture.md`, and domain docs under `docs/` when they apply.
2. Locate existing code with `rg` before introducing new classes or helpers.
3. Make the smallest coherent backend change that fits the current design.
4. Add or update focused tests for behavioral changes.
5. Run a targeted Gradle test when possible, then `./gradlew test` for shared behavior or broad changes.
6. If an API shape changed, update `docs/api.md` in the same change.

## Checks

Use these commands from the repository root:

```bash
./gradlew test
./gradlew test --tests "wlsh.project.intervai.SomeTestClass"
./gradlew test --tests "wlsh.project.intervai.SomeTestClass.methodName"
```
