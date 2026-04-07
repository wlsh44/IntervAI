# 도메인 컨텍스트

## Enum 타입

- **InterviewType**: CS / PORTFOLIO / ALL
- **Difficulty**: ENTRY / JUNIOR / SENIOR
- **InterviewerTone**: FRIENDLY / NORMAL / AGGRESSIVE
- **JobCategory**: FRONTEND, BACKEND, FULLSTACK, ANDROID, IOS, DEVOPS, DATA_ENGINEER, ML_ENGINEER
- **CareerLevel**: ENTRY / JUNIOR / SENIOR
- **QuestionType**: QUESTION / FOLLOW_UP
- **InterviewSessionStatus**: IN_PROGRESS / COMPLETED
- **CsCategory**: DATA_STRUCTURE, ALGORITHM, NETWORK, LANGUAGE, DATABASE
- **MessageRole**: SYSTEM / ASSISTANT / USER
- **MessageType**: ANSWER / FEEDBACK / QUESTION

## 개발 단계

| 단계 | 내용 | 상태 |
|------|------|------|
| Stage 1 | 사용자 인증 (회원가입, 로그인, JWT) | 완료 |
| Stage 2 | 기본 정보 및 포트폴리오 등록 | 완료 |
| Stage 3 | LLM 연동 기본 채팅 (CS 질문 생성) | 완료 |
| Stage 4 | 꼬리 질문 + 실시간 피드백 | 진행 중 |
| Stage 5 | 세션 기록 저장 및 히스토리 UI | 예정 |
| Stage 6 | 종합 리포트 + 포트폴리오 기반 질문 고도화 | 예정 |