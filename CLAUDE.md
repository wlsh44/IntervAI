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

## 참조 문서

- **아키텍처 및 코딩 규칙**: `.claude/architecture.md`
- **도메인 컨텍스트 및 개발 단계**: `.claude/domain.md`
- **API 문서**: `docs/api.md`
- **개발 로드맵**: `docs/roadmap.md`
