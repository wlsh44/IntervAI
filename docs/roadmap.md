# 개발 로드맵

IntervAI의 현재 개발 상태, 진행 중인 작업, 향후 개발 계획을 정리합니다.

---

## 현재 개발 상태 요약

| 단계 | 내용 | 상태 | 비고 |
|------|------|------|------|
| Stage 1 | 사용자 인증 (회원가입, 로그인, JWT) | ✅ 완료 | 비밀번호 강도 정책 미구현 |
| Stage 2 | 프로필 (직군, 경력, 기술스택, 포트폴리오) | ✅ 완료 | - |
| Stage 3 | LLM 연동 기본 채팅 (CS 질문 생성) | ✅ 완료 | - |
| Stage 4 | 꼬리 질문 + 답변 피드백 | 🔧 진행 중 | 세션 finish 구현 중, followUpQuestion 응답 미포함 |
| Stage 5 | 세션 기록 저장 및 히스토리 UI | ⬜ 예정 | - |
| Stage 6 | 종합 리포트 + 포트폴리오 기반 질문 고도화 | ⬜ 예정 | score-report.st 미작성 |

---

## 진행 중인 작업 (Stage 4)

### 1. 세션 종료 엔드포인트 구현 (진행 중)

**현재 상태**: `POST /api/interviews/{interviewId}/finish` 엔드포인트 구현 진행 중. 세션 상태를 `COMPLETED`로 업데이트하는 흐름 개발 중.

**구현 계획**:
- `InterviewSessionManager`에 `complete()` 메서드 추가 (상태 업데이트 + `completedAt` 기록)
- `InterviewSessionValidator`에 `validateSessionInProgress()` 메서드 추가 (IN_PROGRESS 상태 검증)
- `InterviewSessionService`에 `finish()` 메서드 추가 (비즈니스 흐름: 소유자 검증 → 상태 검증 → 완료 처리)
- `InterviewController`에 `finish()` 엔드포인트 추가

**엔드포인트 스펙**:
- **URL**: `POST /api/interviews/{interviewId}/finish`
- **전제 조건**: 세션이 `IN_PROGRESS` 상태여야 함
- **Response**: `200 OK` (body 없음)

**에러 케이스**:

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INTERVIEW_ACCESS_DENIED` | 403 | 타인 면접 접근 (검증 1순위) |
| `SESSION_NOT_FOUND` | 404 | 세션 없음 (검증 2순위) |
| `SESSION_ALREADY_COMPLETED` | 400 | 이미 종료된 세션 (검증 3순위) |

**관련 파일**:
- `session/application/InterviewSessionManager.java` (수정)
- `session/application/InterviewSessionValidator.java` (수정)
- `session/application/InterviewSessionService.java` (수정)
- `interview/presentation/InterviewController.java` (수정)
- `interview/presentation/InterviewControllerTest.java` (수정)

**참고**: `PLAN.md`의 세부 구현 가이드 참조

---

### 2. 답변 응답에 followUpQuestion 포함

**현재 상태**: `AnswerResult`가 `feedback`만 반환. 꼬리 질문 내용을 프론트에 전달하지 않음.

**해결 방안**:
- `AnswerResult`에 `followUpQuestion` 필드 추가
- `AnswerService.decideNextQuestion()`에서 생성된 꼬리 질문 내용을 반환값에 포함
- `CreateAnswerResponse` DTO 업데이트

**관련 파일**:
- `answer/domain/AnswerResult.java`
- `answer/presentation/dto/CreateAnswerResponse.java`
- `answer/application/AnswerService.java`

---

## 즉시 해결 필요 (단기 우선순위)

### 1. 비밀번호 강도 정책 추가

**현재 상태**: `CreateUserRequest`에 길이 검증(`4~12자`)만 존재.

**우선순위**: Low (기능성에 영향 없음)

**해결 방안**:
- `@Pattern`으로 영문+숫자 조합 등 정책 적용

**관련 파일**:
- `user/presentation/dto/CreateUserRequest.java`

---

## Stage 5 계획: 세션 히스토리 및 Message 엔티티

목적: ChatGPT 스타일의 세션 목록 조회, 세션 상세 확인, 이어하기, 삭제 기능 구현

### 주요 작업
1. **Message 엔티티 설계** — 질문·답변·피드백·꼬리질문을 시퀀스 기반으로 저장
2. **세션 히스토리 저장** — 면접 진행 중 모든 메시지를 Message 테이블에 기록
3. **세션 히스토리 조회 API** — 목록 및 상세 조회 엔드포인트 구현
4. **UI 표현 준비** — 프론트에서 히스토리 렌더링할 데이터 형식 정의

### 기능 요구사항

| 기능 | 엔드포인트 | 우선순위 | 예정 상태 |
|------|----------|---------|---------|
| 세션 목록 조회 (페이지네이션, 타입/날짜 필터) | `GET /api/sessions` | High | ⬜ 예정 |
| 세션 상세 조회 (질문·답변·피드백 히스토리) | `GET /api/sessions/{sessionId}` | High | ⬜ 예정 |
| 세션 이어하기 | `POST /api/sessions/{sessionId}/resume` | Medium | ⬜ 예정 |
| 세션 삭제 | `DELETE /api/sessions/{sessionId}` | Low | ⬜ 예정 |

### 도메인 모델 설계

#### 기존 확장
```
InterviewSession
  - totalScore: Integer (nullable, 세션 완료 후 저장)
  - createdAt, completedAt: LocalDateTime 활용
```

#### 신규 엔티티
```
Message (신규)
  - id: Long
  - interviewSessionId: Long (FK)
  - role: MessageRole (SYSTEM / ASSISTANT / USER)
  - type: MessageType (QUESTION / ANSWER / FEEDBACK)
  - content: Text (최대 5000자 이상)
  - sequence: Integer (세션 내 순서, 0부터 시작)
  - questionId: Long (FK, nullable - FEEDBACK일 때 null 가능)
  - createdAt: LocalDateTime
```

#### Enum 정의
```
MessageRole: SYSTEM, ASSISTANT, USER
MessageType: QUESTION (질문), ANSWER (답변), FEEDBACK (피드백)
```

### 패키지 구조

```
session/
├── presentation/
│   └── dto/
│       ├── SessionListResponse.java
│       └── SessionDetailResponse.java
├── application/
│   ├── SessionHistoryService.java
│   ├── SessionHistoryFinder.java
│   └── SessionHistoryManager.java
└── infra/
    └── InterviewSessionRepository.java (메서드 추가)
```

---

## Stage 6 계획: 종합 리포트 + 포트폴리오 기반 질문 고도화

### 6-1. 종합 리포트 생성

#### 기본 정보
| 항목 | 내용 |
|------|------|
| 엔드포인트 | `GET /api/interviews/{interviewId}/report` |
| 전제 조건 | 세션 상태가 `COMPLETED`여야 함 |
| LLM 프롬프트 | `resources/prompts/score-report.st` (신규 작성 필요) |
| 상태 | ⬜ 예정 |

#### 리포트 생성 흐름
1. 프론트에서 세션 종료 후 `GET /api/interviews/{interviewId}/report` 요청
2. 백엔드가 해당 세션의 모든 Q&A 히스토리 조회
3. Claude API에 `score-report.st` 프롬프트로 종합 평가 요청
4. 점수, 강점, 개선점 포함한 리포트 반환

#### score-report.st 출력 형식 (JSON)

```json
{
  "totalScore": 85,
  "scores": {
    "conceptUnderstanding": 90,
    "problemSolving": 80,
    "communication": 85
  },
  "strengths": ["핵심 개념 이해도 높음", "논리적 전개"],
  "improvements": ["깊이 있는 예시 부족", "엣지 케이스 언급 필요"],
  "overallComment": "전반적으로 기본기가 탄탄합니다."
}
```

#### 패키지 구조

```
report/
├── domain/
│   └── ScoreReport.java
├── application/
│   ├── ReportService.java
│   ├── ReportFinder.java
│   └── ReportGenerator.java        # Claude API 호출
├── infra/
│   ├── ClaudeReportGenerator.java
│   └── ReportRepository.java (선택사항)
└── presentation/
    ├── ReportController.java
    └── dto/ReportResponse.java
```

---

### 6-2. 포트폴리오 기반 질문 고도화 (중기 계획)

#### 현재 상태
`portfolioLinks`(URL 목록)를 프롬프트에 주입하는 수준

#### 목표
GitHub 레포지토리 분석 → 기술 스택, README 파싱 → 맞춤형 질문 생성

#### 구현 접근법
1. **GitHubClient** — GitHub REST API 호출
   - 레포지토리 메타데이터 조회
   - README 파일 다운로드
   - 주요 파일 목록 조회

2. **RepositoryAnalyzer** — 기술 스택 감지
   - `package.json` (Node.js/NPM), `pom.xml` (Java/Maven), `requirements.txt` (Python) 등 분석
   - 프로젝트 설명 및 주요 기술 추출

3. **프롬프트 확장**
   - `question-generator.st`에 분석 결과 변수 추가
   - 포트폴리오 유형에서 GitHub URL 자동 분석

#### 패키지 구조
```
github/
├── application/
│   ├── RepositoryAnalyzer.java
│   └── RepositoryAnalysisResult.java
└── infra/
    └── GitHubRestClient.java
```

#### 상태
⬜ 예정 (4주 이상 소요)

---

## 기술적 개선 사항 (장기)

### 1. 에러 처리 및 재시도 정책

**현재 상태**: LLM 호출 실패 시 즉시 500 에러

**목표**: 안정성 및 사용자 경험 개선

**개선 방안**:
- Resilience4j (또는 Spring Retry)로 자동 재시도
  - 재시도 횟수: 3회
  - Backoff 전략: exponential (초기 대기 시간 1초, 최대 10초)
- LLM 호출 타임아웃 설정 (30초)
- 사용자 친화적 에러 메시지 반환 (기술 스택 노출 최소화)

**적용 대상**:
- `ClaudeQuestionGenerator` — 질문 생성 호출
- `ClaudeFeedbackGenerator` — 피드백/꼬리질문 호출
- `ClaudeReportGenerator` — 리포트 생성 호출

**우선순위**: 중기 (Stage 6 완료 후)

---

### 2. 캐싱 전략

**목표**: 성능 최적화 및 API 호출 감소

**적용 대상 및 TTL**:
- **사용자 프로필**: TTL 1시간 (프로필 수정 시 즉시 invalidate)
- **GitHub 분석 결과**: TTL 24시간 (포트폴리오 업데이트 시 invalidate)
- **TechStack 목록**: TTL 7일 (변경 빈도 낮음)

**구현**:
- Redis를 캐시 백엔드로 사용 (이미 Refresh Token 저장에 사용 중)
- Spring Cache Abstraction 적용 (`@Cacheable`, `@CacheEvict`)

**우선순위**: 장기 (Stage 6 완료 후, 성능 이슈 발생 시 우선 추진)

---

## 우선순위 및 일정 계획

### Stage 4 (진행 중)
| 항목 | 상태 | 영향도 | 난이도 | 예상 일정 |
|------|------|--------|--------|----------|
| 세션 finish 엔드포인트 | 🔧 진행 중 | High | Low | 현재 주 |
| followUpQuestion 응답 포함 | ⬜ 예정 | High | Low | 다음 주 |

### Stage 5 (예정)
| 항목 | 상태 | 영향도 | 난이도 | 예상 일정 |
|------|------|--------|--------|----------|
| 세션 히스토리 목록/상세 | ⬜ 예정 | High | Medium | 2-3주 |
| Message 엔티티 설계 및 생성 | ⬜ 예정 | High | Medium | 2-3주 |

### Stage 6 (예정)
| 항목 | 상태 | 영향도 | 난이도 | 예상 일정 |
|------|------|--------|--------|----------|
| score-report.st 작성 | ⬜ 예정 | High | Low | 3-4주 |
| 리포트 생성 API | ⬜ 예정 | High | Medium | 3-4주 |
| GitHub 분석 기능 (포트폴리오 기반 질문 고도화) | ⬜ 예정 | Medium | High | 4주 이상 |

### 기타 개선 사항
| 항목 | 상태 | 영향도 | 난이도 | 우선순위 |
|------|------|--------|--------|---------|
| 비밀번호 강도 정책 | ⬜ 예정 | Low | Low | 장기 |
| 세션 이어하기/삭제 | ⬜ 예정 | Medium | Medium | 중기 |
| 에러 처리/재시도 (Resilience4j) | ⬜ 예정 | Medium | Low | 중기 |
| 캐싱 (Redis) | ⬜ 예정 | Low | Low | 장기 |

---

## 개발 단계별 체크리스트

### Stage 4 (현재)
- [ ] 세션 종료 엔드포인트 구현 (`POST /api/interviews/{interviewId}/finish`)
- [ ] AnswerResult에 followUpQuestion 필드 추가 및 응답 포함

**완료 시 요구사항**:
- 세션 COMPLETED 상태로 전환 기능 동작
- 꼬리 질문이 프론트에 전달됨
- 모든 테스트 통과

### Stage 5 (다음)
- [ ] Message 엔티티 설계 및 생성
- [ ] 세션 진행 중 Message 저장 로직 추가
- [ ] 세션 히스토리 목록 조회 API (`GET /api/sessions`)
- [ ] 세션 히스토리 상세 조회 API (`GET /api/sessions/{sessionId}`)

**완료 시 요구사항**:
- 과거 세션 목록 조회 가능
- 과거 세션의 모든 Q&A 히스토리 조회 가능

### Stage 6 (이후)
- [ ] score-report.st 프롬프트 작성
- [ ] 리포트 생성 API (`GET /api/interviews/{interviewId}/report`)
- [ ] (선택) 포트폴리오 기반 질문 고도화 (GitHub 분석)

**완료 시 요구사항**:
- 종합 리포트 생성 및 조회 가능
- 점수, 강점, 개선점 피드백 제공

---

## 관련 문서

- [PROJECT_CONTEXT.md](../PROJECT_CONTEXT.md) — 전체 프로젝트 요구사항 및 기술 스택
- [CLAUDE.md](../CLAUDE.md) — 아키텍처 및 코딩 컨벤션
- [PLAN.md](../PLAN.md) — 현재 진행 중인 Stage 4 구현 상세 가이드
- [api.md](api.md) — REST API 레퍼런스
- [interview-session.md](interview-session.md) — 면접 세션 기능 명세
- [ai-interview.md](ai-interview.md) — AI 면접 기능 명세
- [session-history.md](session-history.md) — 세션 히스토리 기능 명세
- [report.md](report.md) — 종합 리포트 기능 명세