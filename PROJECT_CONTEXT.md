# AI 면접 연습 앱 — 프로젝트 컨텍스트

## 프로젝트 개요

LLM(Claude API)을 연동한 AI 면접 연습 웹 애플리케이션.
사용자가 포트폴리오를 제출하면 그에 맞는 맞춤형 면접 질문을 생성하고,
답변에 대한 피드백과 꼬리 질문을 제공한다.
ChatGPT처럼 면접 대화 기록을 저장하고 이어볼 수 있다.

---

## 기술 스택

| 레이어        | 기술                                              |
|------------|-------------------------------------------------|
| 백엔드        | Spring Boot 3.5, Java 21, Gradle 8.14           |
| LLM 연동     | SpringAI + Anthropic Claude API (스트리밍)          |
| 인증         | Spring Security, jjwt 0.12.6                    |
| 캐시 / 임시 저장 | Redis (Refresh Token 저장)                        |
| 영구 저장      | MySQL (+ H2 테스트용)                               |
| ORM        | JPA                                             |
| 파일 처리      | AWS S3                                          |
| 주요 라이브러리   | Lombok, Bean Validation                         |
| 테스트        | JUnit 5, REST Assured (MockMvc), Embedded Redis |
| 프론트엔드      | React + TypeScript + Vite                       |
| 상태 관리      | Zustand                                         |
| 서버 상태      | TanStack Query                                  |
| UI         | Tailwind CSS + shadcn/ui                        |
| PWA        | vite-plugin-pwa                                 |

---

## 요구사항

### 1. 사용자 시스템

#### 인증
- 닉네임 / 비밀번호 회원가입 및 로그인
- JWT 기반 세션 관리

#### 프로필
- 닉네임, 프로필 사진
- 목표 직군 설정 (프론트엔드 / 백엔드 / 풀스택 / 데이터 등)
- 경력 수준 설정 (신입 / 주니어 / 시니어)

#### 포트폴리오 등록
- GitHub URL 연동 → 레포지토리 목록 파싱
- 기술 스택 수동 태그 입력
- 등록된 포트폴리오 데이터는 LLM 시스템 프롬프트에 주입되어 맞춤 질문 생성에 사용

---

### 2. 면접 세션

#### 세션 생성 옵션
- 면접 유형: CS 기초 / 포트폴리오 기반 / 종합
- 난이도: 신입 · 주니어 · 시니어
- 예상 질문 수
- 꼬리 질문 수(최대 최소)
- 직군
- 포트폴리오 링크(github url, 포트폴리오 기반 or 종합인 경우)
- 기본값은 유저의 프로필 및 포트폴리오에 등록된 값이나 생성 시기에 변경 가능

#### 진행 방식
- LLM과의 채팅 인터페이스
- 답변 제출 → 다음 질문 요청 흐름
- 답변마다 피드백이 존재하지만 유저가 확인하고싶을 때 확인하는 방식

#### 세션 기록 저장 (ChatGPT 히스토리 방식)
- 세션 목록 사이드바: 날짜, 면접 유형, 점수 요약 표시
- 대화 전체 (질문 · 답변 · 피드백) 영구 저장
- 키워드 검색, 날짜 · 유형 필터
- 기존 세션 이어서 연습 가능
- 세션 삭제

---

### 3. AI 면접 기능

#### 질문 생성
- **포트폴리오 기반 질문**: GitHub 레포지토리의 기술 스택, 커밋 패턴, README를 분석하여 맞춤 질문 생성
    - 예: "이 프로젝트에서 Redux를 선택한 이유는?"
- **CS 기초 질문 풀**: 자료구조 · 알고리즘, 네트워크, 운영체제, 데이터베이스, 직군별 심화 (React 내부 동작, JVM 등)
- **꼬리 질문 (follow-up)**: 사용자 답변을 분석해 답변 내용에 연계된 추가 질문 생성
    - 예: "방금 언급한 캐싱 전략에서 cache invalidation은 어떻게 처리했나요?"

#### 답변 피드백
- 답변 후 유저가 원할 때 피드백: 핵심 키워드 포함 여부, 논리 구조, 깊이 평가
- 이상적인 모범 답변 예시 제공 (피드백 요청 시)
- 세션 종료 후 종합 리포트: 전체 점수, 강점 / 약점, 개선 방향 요약

#### LLM 컨텍스트 관리
- 시스템 프롬프트에 면접관 페르소나 설정 (엄격한 기술 면접관, 편안한 문화 면접관 등)
- 세션 내 전체 대화 히스토리를 컨텍스트로 유지 → 꼬리 질문 품질의 핵심
- 포트폴리오 데이터 (GitHub 분석 결과, 기술 스택)를 시스템 프롬프트에 주입

---

## 면접 API 시퀀스 플로우

### 컨텍스트
이 문서는 AI 면접 연습 앱의 백엔드-프론트엔드 간 API 시퀀스 플로우를 정의한다.
구현 시 이 플로우를 기준으로 코드를 작성하라.

### 액터
- Frontend (React + TypeScript)
- Backend (Spring Boot)
- Claude API (질문 생성 / 피드백+꼬리질문 / 종합 리포트)

---

### ① 면접 설정 및 질문 사전 생성

1. Frontend → Backend : POST /api/interviews/setup
    - body: { topic, level, count, interviewerTone }
    - Interview 도메인 생성

2. Frontend -> Backend : POST /api/interviews/sessions
    - body: { interviewId }
    - InterviewSession 도메인 생성

3. Backend → Claude API : 질문 {count}개 일괄 생성 요청
    - 프롬프트: question-generate.st 사용
    - 변수: { topic, level, count, interviewerTone }

4. Claude API → Backend : questions[] 반환 (JSON 배열)

5. Backend → DB : 세션 저장
    - { interviewId, questions[], currentIdx: 0, history: [], status: IN_PROGRESS }

6. Backend → Frontend : 201 { interviewId }
    - 질문 목록은 프론트에 내려주지 않음

---

### ② 질문 하나씩 노출 (채팅형)

1. Frontend → Backend : GET /api/interviews/{id}/next

2. Backend → DB : questions[currentIdx] 조회

3. Backend → Frontend : 200 { question, questionIdx, total }

---

### ③ 답변 제출 → 피드백 + 꼬리질문 생성

1. Frontend → Backend : POST /api/interviews/{id}/answers
    - body: { questionId, answerText, isFollowUp }

2. Backend → DB : 세션 + 대화 히스토리 조회

3. Backend → Claude API : 피드백 + 꼬리질문 요청
    - 프롬프트: feedback-followup.st 사용
    - 변수: { topic, level, interviewerTone, question, answerText }
    - 히스토리: Spring AI ChatMemory 어드바이저가 자동 관리
    - conversationId: sessionId 로 면접 세션 단위 분리

4. Claude API → Backend : { feedback, followUpQuestion }

5. Backend → DB : 답변 + 피드백 저장 (history[] append)

6. Backend → Frontend : 200 { feedback, followUpQuestion }
    - Frontend: feedback은 Zustand에 보관, UI에는 숨김 처리
    - Frontend: "피드백 보기" 버튼 클릭 시 로컬 상태에서 꺼내 표시

#### 꼬리질문 답변
- ③과 동일한 흐름 반복
- isFollowUp: true 로 구분
- 같은 conversationId 유지 (ChatMemory가 이어서 관리)

---

### ④ 다음 본 질문으로 이동

1. Frontend → Backend : GET /api/interviews/{id}/next

2. Backend → DB : currentIdx++ 후 questions[currentIdx] 조회
    - 이 시점에 ChatMemory conversationId를 새로 발급하여 이전 꼬리질문 컨텍스트와 분리

3. Backend → Frontend : 200 { question, questionIdx, total }

→ ② ~ ③ 반복

---

### ⑤ 세션 종료 & 종합 리포트

1. Frontend → Backend : POST /api/interviews/{id}/finish

2. Backend → DB : 전체 history + feedbacks 조회

3. Backend → Claude API : 종합 평가 리포트 요청
    - 프롬프트: score-report.st 사용
    - 변수: { topic, level, interviewerTone, history }

4. Claude API → Backend : scoreReport
    - { totalScore, scores: { conceptUnderstanding, problemSolving, communication }, strengths[], improvements[], overallComment }

5. Backend → DB : status를 COMPLETED로 업데이트

6. Backend → Frontend : 200 { scoreReport, questions[{ question, answer, feedback }] }

---

### 프롬프트 파일 구조
resources/prompts/
question-generate.st   # ① 질문 생성
feedback-followup.st   # ③ 피드백 + 꼬리질문
score-report.st        # ⑤ 종합 리포트

### 주요 설계 결정
- 질문은 세션 생성 시 전체를 한 번에 생성해 DB에 저장, 프론트엔 1개씩 노출
- 피드백은 답변 제출 응답에 포함하지만 UI에서 숨김 처리 (추가 API 호출 없음)
- 꼬리질문 히스토리는 Spring AI ChatMemory 어드바이저가 관리 (프롬프트에 별도 삽입 안 함)
- 본 질문이 바뀔 때 conversationId를 새로 발급해 꼬리질문 컨텍스트 분리
- 세션 종료 시 DB status를 COMPLETED로 업데이트

---

## 현재 구현 상태 (Stage 1 완료, Stage 2 진행 중)

### user 도메인
- 회원가입: 닉네임/비밀번호 검증, BCrypt 암호화
- `UserService` → `UserValidator` (중복 닉네임 검증) + `UserManager` (생성/저장)

### common/auth
- JWT 토큰 발급/갱신: Access Token (24h), Refresh Token (7d, Redis 저장)
- `AuthService` → `RefreshTokenValidator` + `RefreshTokenRotator` + `TokenPairGenerator`
- Refresh Token은 HttpOnly 쿠키로 관리 (`RefreshTokenCookieHandler`)
- `AccessTokenProvider`, `RefreshTokenProvider`, `JwtProvider`로 토큰 생성/파싱 분리
- `JwtAuthenticationFilter` — Bearer 토큰 파싱 → `UserInfo`를 SecurityContext에 설정

### profile 도메인
- 프로필 수정/조회: 희망 직무, 경력 단계, 기술 스택(다대다), 포트폴리오 링크
- `ProfileService` → `ProfileValidator` (소유자 검증) + `ProfileManager` (CUD) + `ProfileFinder` (조회)
- `TechStackManager` — 기술 스택 findOrCreate (기존 것 재사용, 없으면 생성)
- 본인 프로필 검증: `ProfileValidator.validateProfileOwner()` → 타인 접근 시 `PROFILE_ACCESS_DENIED` (403)
- 기술 스택은 `TechStackEntity` (공유) + `ProfileTechStackEntity` (매핑)로 다대다 관계 관리
- 포트폴리오 링크는 `PortfolioLinkEntity`로 1:N 관계

### common 인프라
- `BaseEntity` (soft delete, `EntityStatus` 기반 상태 전이)
- `SecurityConfig` (Spring Security 설정)
- `CustomException` + `ErrorCode` + `GlobalExceptionHandler` (전역 예외 처리)
- `JpaAuditingConfig` (createdAt, modifiedAt 자동 관리)

### 구현된 API 엔드포인트

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/users/sign-up` | 회원가입 | X |
| POST | `/api/users/login` | 로그인 | X |
| POST | `/api/auth/refresh` | Access Token 갱신 (Refresh Token 쿠키 기반) | X |
| PUT | `/api/profile/{profileId}` | 프로필 수정 (본인 검증) | O |
| GET | `/api/profile/{profileId}` | 프로필 조회 (본인 검증) | O |

### 현재 패키지 구조

```
wlsh.project.intervai
├── common
│   ├── auth
│   │   ├── application/    # AuthService, JwtProvider, TokenPairGenerator, RefreshToken*
│   │   ├── domain/         # TokenPair, UserInfo
│   │   ├── infra/          # RefreshTokenRedisRepository
│   │   └── presentation/   # AuthController, dto/, cookie/, filter/JwtAuthenticationFilter
│   ├── config/             # SecurityConfig, JpaAuditingConfig
│   ├── entity/             # BaseEntity, EntityStatus
│   └── exception/          # CustomException, ErrorCode, ErrorResponse, GlobalExceptionHandler
├── user
│   ├── application/        # UserService, UserManager, UserValidator
│   ├── domain/             # User, CreateUserCommand, CreateUserResult, LoginCommand, LoginResult
│   ├── infra/              # UserEntity, UserRepository
│   └── presentation/       # UserController, dto/
└── profile
    ├── application/        # ProfileService, ProfileManager, ProfileFinder, ProfileValidator, TechStackManager
    ├── domain/             # Profile, CreateProfileCommand, UpdateProfileCommand, JobCategory, CareerLevel
    ├── infra/              # ProfileEntity, ProfileRepository, TechStackEntity, TechStackRepository,
    │                       #   ProfileTechStackEntity, ProfileTechStackRepository, PortfolioLinkEntity, PortfolioLinkRepository
    └── presentation/       # ProfileController, dto/ProfileResponse, dto/UpdateProfileRequest
```

---

## 주요 도메인 모델

### 구현 완료

```
User                          # user 도메인
  - id
  - nickname (unique)
  - passwordHash

Profile                       # profile 도메인, User와 1:1
  - id
  - userId (unique)
  - jobCategory: JobCategory
  - careerLevel: CareerLevel
  - techStacks: List<String>       # TechStack + ProfileTechStack으로 다대다 관리
  - portfolioLinks: List<String>   # PortfolioLink로 1:N 관리

TechStack                     # 공유 기술 스택 (여러 프로필에서 재사용)
  - id
  - name (unique, max 100)

ProfileTechStack              # Profile ↔ TechStack 매핑 테이블
  - id
  - profileId
  - techStackId

PortfolioLink                 # 프로필별 포트폴리오 URL
  - id
  - profileId
  - url (max 500)

모든 Entity는 BaseEntity 상속: status (ACTIVE/DELETED), createdAt, modifiedAt
```

### Enum 정의

```
JobCategory: FRONTEND, BACKEND, FULLSTACK, ML_ENGINEER, ANDROID, IOS, DATA_ENGINEER, DEVOPS
CareerLevel: ENTRY, JUNIOR, SENIOR
```

### 미구현 (예정)

```
InterviewSession
  - id, userId, profileId
  - type (CS / PORTFOLIO / ALL)
  - difficulty (ENTRY / JUNIOR / SENIOR)
  - status (IN_PROGRESS / COMPLETED)
  - totalScore
  - completedAt

Message
  - id, sessionId
  - role (SYSTEM / ASSISTANT / USER)
  - type (ANSWER / FEEDBACK / QUESTION)
  - content
  - sequence
```

---

## 개발 우선순위 (단계별)

| 단계 | 내용 | 상태 |
|------|------|------|
| Stage 1 | 사용자 인증 (회원가입, 로그인, JWT 토큰 발급/갱신) | ✅ 완료 |
| Stage 2 | 기본 정보 및 포트폴리오 등록 | 🔧 진행 중 |
| Stage 3 | LLM 연동 기본 채팅 (CS 질문 생성) | ✅ 완료 |
| Stage 4 | 꼬리 질문 + 실시간 피드백 | ⬜ 예정 |
| Stage 5 | 세션 기록 저장 및 히스토리 UI | ⬜ 예정 |
| Stage 6 | 종합 리포트 + 포트폴리오 기반 질문 고도화 | ⬜ 예정 |
