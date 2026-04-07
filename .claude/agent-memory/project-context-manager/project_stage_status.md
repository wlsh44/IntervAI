---
name: 개발 단계별 구현 상태
description: IntervAI 각 개발 단계의 구현 완료 여부 및 주요 도메인 구조 (2026-04-07 기준)
type: project
---

Stage 1 (인증) — 완료. 비밀번호 정책 4~12자 적용(`INVALID_PASSWORD_LENGTH`), 닉네임 4~8자(`INVALID_NICKNAME_LENGTH`).

Stage 2 (프로필) — 완료. 프로필 CRUD, 본인 검증(`PROFILE_ACCESS_DENIED`), TechStack 다대다, PortfolioLink 1:N.

Stage 3 (LLM 기본 채팅) — 완료.
- `POST /api/interviews` — 면접 설정 생성 (InterviewType, Difficulty, InterviewerTone, CsSubject, portfolioLinks)
- `POST /api/interviews/{id}/sessions` — 세션 생성
- `POST /api/interviews/{id}/questions` — LLM 질문 일괄 생성
- `GET /api/interviews/{id}/questions/current` — 현재 질문 조회

Stage 4 (꼬리 질문 + 피드백) — 진행 중.
- `POST /api/interviews/{id}/answers` — 답변 제출, 피드백 + 꼬리 질문 생성 (구현)
- `POST /api/interviews/{id}/finish` — 세션 종료, COMPLETED 업데이트 (구현)
- 미구현: conversationId 재발급으로 꼬리 질문 컨텍스트 분리

Stage 5 (세션 기록) — 미구현
Stage 6 (종합 리포트) — 미구현

**Why:** 현재 feat/interview 브랜치에서 Stage 3-4 작업 진행 중.

**How to apply:** 문서 업데이트 또는 기능 논의 시 위 상태를 기준으로 판단.
