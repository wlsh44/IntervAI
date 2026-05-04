# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> **200줄 이하 유지 원칙**: 세부 지침은 각 skill 파일에 위임한다. 이 파일에는 흐름(what & when)과 공통 원칙만 유지한다.

## Project Overview

IntervAI — AI 면접 연습 웹 애플리케이션. Claude API(SpringAI)를 활용하여 포트폴리오 기반 맞춤형 면접 질문 생성, 답변 피드백, 꼬리 질문을 제공하는 서비스.

## Tech Stack

- **Backend**: Spring Boot 3.5, Java 21, Gradle 8.14
- **LLM**: SpringAI + Anthropic Claude API
- **DB**: MySQL (영구 저장), Redis (캐시/임시 저장)
- **File Storage**: AWS S3
- **Frontend** (별도): React + TypeScript + Vite, Zustand, TanStack Query, Tailwind CSS + shadcn/ui
- **Libraries**: Lombok

## Build & Run Commands

```bash
./gradlew build              # Build
./gradlew bootRun            # Run application
./gradlew test               # Run all tests
./gradlew test --tests "wlsh.project.intervai.SomeTestClass"
./gradlew test --tests "wlsh.project.intervai.SomeTestClass.methodName"
./gradlew clean build        # Clean build
```

## Agent Traps

> **모든 작업 시작 전 반드시 읽을 것.** 상세 내용은 `docs/agent-traps.md` 참조.

| 패턴 | 잘못된 방법 | 올바른 방법 |
|---|---|---|
| 브랜치 없이 구현 시작 | main에서 파일 수정 후 커밋 | 첫 Edit/Write 전 `git checkout -b feature/issue-{n}-backend` 실행 |

## 공통 원칙

- `docs/api.md`는 단일 진실 공급원. 사용자 승인 없이 변경 금지
- 계획에 없던 파일 발견 시 즉시 중단 + 보고
- 예상 커밋 5개 초과 시 즉시 중단 + 작업 분해 제안
- Compact는 skill 간 전환 시 기본 실행
- CLAUDE.md는 200줄 이하 유지. 초과 시 `docs/`로 분리 후 링크

---

## Workflow

> 모든 작업은 아래 순서를 반드시 따른다.
> 단계를 건너뛰거나 사용자 승인 없이 다음 단계로 진행하지 않는다.
> 각 단계의 세부 지침은 해당 skill 파일을 따른다.

### [1] 작업 준비 — `intervai-start`

`.agent/skills/intervai-start/SKILL.md` 실행.

- GitHub 이슈 확인 또는 생성
- 필독 파일 읽기 + 요약 출력 (skill 내 조건에 따라 선택적으로)
- Plan 모드 진입 조건 판단 → 사용자 승인 후 진행
- 작업 분해 조건 판단 → 사용자 승인 후 진행

### [2] 브랜치 및 Worktree 설정

- 백엔드: `feature/issue-{n}-backend` / 프론트엔드: `feature/issue-{n}-frontend`
- `claude --worktree` 플래그로 격리된 worktree 생성
- 각 worktree에서 개발 환경 초기화

### [3] 구현 — `intervai-be` / `intervai-fe`

`.agent/skills/intervai-be/SKILL.md` 또는 `.agent/skills/intervai-fe/SKILL.md` 실행.

- skill 내 구현 순서, 커밋 단위, Compact 시점, 즉시 중단 조건을 따른다.
- 즉시 중단 조건 발생 시 → 중단 + 보고. 혼자 판단해서 진행 금지.

### [4] 코드 리뷰

- `/codex:review --background` 실행
- 문제 목록 출력 → 수정 → 재확인

### [5] 커밋 · 푸시 · PR — `intervai-ship`

`.agent/skills/intervai-ship/SKILL.md` 실행.

- 자동 테스트 → 수동 테스트 게이트 (사용자 승인 필수) → 커밋 정리 → PR 생성
- 수동 테스트 게이트 생략 금지
- 백엔드·프론트엔드 PR 분리 필수

### [6] PR 리뷰 반영

- PR 리뷰 확인 → 수정 필요 여부 판단
- 수정 필요 시 → 해당 worktree 복귀 → 수정 → push → resolve
- 수정 불필요 시 → resolve 처리

### [7] 작업 종료 — 문서 최신화

`intervai-ship` skill의 Post-Ship 단계에서 자동 실행됨. 별도 수행 불필요.

---

## 참조 문서

- **아키텍처 및 코딩 규칙**: `docs/architecture.md`
- **API 문서**: `docs/api.md`
- **개발 로드맵**: `docs/roadmap.md`
- **Agent Traps 상세**: `docs/agent-traps.md`
- **작업 로그**: `docs/notes/`
