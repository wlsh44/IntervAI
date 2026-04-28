# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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
./gradlew test --tests "wlsh.project.intervai.SomeTestClass"            # Single test class
./gradlew test --tests "wlsh.project.intervai.SomeTestClass.methodName" # Single test method
./gradlew clean build        # Clean build
```

## 기능 개발 워크플로우

> 현재 작업 기준은 skill 문서입니다. 시작 시 `.agent/skills/intervai-start/SKILL.md`, 백엔드는 `.agent/skills/intervai-be/SKILL.md`, 프런트엔드는 `.agent/skills/intervai-fe/SKILL.md`, ship은 `.agent/skills/intervai-ship/SKILL.md`를 먼저 확인하세요.
> Claude 호환용 skill은 `.claude/skills/intervai-*`에 두고 canonical skill을 참조합니다.

```
[1] intervai-start skill로 요구사항에 맞는 GitHub issue 확인 또는 생성
[2] 작업 영역에 맞는 skill 확인 (.agent/skills/intervai-be 또는 .agent/skills/intervai-fe)
[3] 요구사항(docs) 및 docs/api.md 확인
[4] 브랜치 이동(feat/{도메인} 또는 fe/feat/{도메인})
[5] skill 지침에 맞게 직접 구현 및 테스트
[6] 코드 리뷰(/codex:review --background 사용 가능)
[7] intervai-ship skill로 테스트 → 커밋 → push → PR 생성/업데이트
[8] PR 리뷰 확인 → 수정 필요 여부 판단 → 수정/resolve/push
```


## 참조 문서

- **아키텍처 및 코딩 규칙**: `docs/architecture.md`
- **API 문서**: `docs/api.md`
- **개발 로드맵**: `docs/roadmap.md`
