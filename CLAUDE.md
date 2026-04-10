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

```
[1] 요구사항(docs) 체크
[2] docs/api.md 조회 -> dev-planner로 계획 설정
[3] 브랜치 이동(feat/{도메인}) 및 plan-executor 구현 
[4] 코드 리뷰(/codex:review --background 사용)
[5] 논리적 단위로 커밋 분리 → git push
[6] gh pr create → PR 생성 (base: main)
[7] PR 리뷰 확인 → 수정 필요 여부 판단 (필요 없으면 코멘트 전부 resolve 처리) 
[8] 수정할 경우 [3], [4] 다시 진행 → push 및 해결된 코멘트 resolve
```


## 참조 문서

- **아키텍처 및 코딩 규칙**: `.claude/architecture.md`
- **도메인 컨텍스트 및 개발 단계**: `.claude/domain.md`
- **API 문서**: `docs/api.md`
- **개발 로드맵**: `docs/roadmap.md`
