# IntervAI — 문서 인덱스

AI 면접 연습 웹 애플리케이션 IntervAI의 기능 명세 문서 목록입니다.
각 문서는 기능 도메인별로 분리되어 있으며, `PROJECT_CONTEXT.md`의 요구사항을 기반으로 작성되었습니다.

---

## 개발 단계 현황

| 단계 | 내용 | 상태 |
|------|------|------|
| Stage 1 | 사용자 인증 (회원가입, 로그인, JWT 토큰 발급/갱신) | 완료 |
| Stage 2 | 기본 정보 및 포트폴리오 등록 | 완료 |
| Stage 3 | LLM 연동 기본 채팅 (CS 질문 생성) | 완료 |
| Stage 4 | 꼬리 질문 + 실시간 피드백 | 완료 (세션 finish 미구현) |
| Stage 5 | 세션 기록 저장 및 히스토리 UI | 예정 |
| Stage 6 | 종합 리포트 + 포트폴리오 기반 질문 고도화 | 예정 |

---

## 문서 목록

| 파일 | 기능 도메인 | 관련 개발 단계 |
|------|------------|--------------|
| [auth.md](auth.md) | 사용자 인증 (회원가입, 로그인, JWT 관리) | Stage 1 |
| [profile.md](profile.md) | 사용자 프로필 (직군, 경력, 기술 스택, 포트폴리오 링크) | Stage 2 |
| [interview-session.md](interview-session.md) | 면접 세션 생성 및 진행 (설정, 질문 노출, 답변 제출) | Stage 3, 4, 5 |
| [ai-interview.md](ai-interview.md) | AI 면접 기능 (질문 생성, 꼬리 질문, 답변 피드백, LLM 컨텍스트 관리) | Stage 3, 4, 6 |
| [session-history.md](session-history.md) | 세션 기록 및 히스토리 (저장, 조회, 검색, 필터, 이어하기, 삭제) | Stage 5 |
| [report.md](report.md) | 종합 리포트 (점수 산출, 강점/약점, 개선 방향) | Stage 6 |
| [api.md](api.md) | REST API 레퍼런스 (엔드포인트, Request/Response, 에러 코드) | 전체 |

---

## 관련 파일

- `/PROJECT_CONTEXT.md` — 전체 프로젝트 요구사항 및 현재 구현 상태
- `/CLAUDE.md` — 아키텍처 및 코딩 컨벤션 가이드
