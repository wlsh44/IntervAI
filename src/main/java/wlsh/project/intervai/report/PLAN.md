# 종합 리포트 기능 개발 계획

## 현재 구현 점검 (2026-04-20)

### 완료
- [x] 답변 제출 시 `score` 저장 및 응답 반영
- [x] 리포트 도메인/DTO를 신규 스펙(`totalScore`, `overallComment`, `questions[].keywords`) 기준으로 재설계
- [x] `summary.st` 및 ReportGenerator를 대화 컨텍스트 기반 스키마로 정리
- [x] `InterviewReportEntity`를 `questionsJson` 중심 저장 구조로 정리
- [x] Service 계층에서 조회/검증/조립 책임을 Finder/Validator/Assembler로 분리
- [x] `InterviewReportManager`는 생성만, `InterviewReportFinder`는 조회만 담당하도록 분리
- [x] `GET /api/interviews/{interviewId}/report` 응답 DTO 및 컨트롤러 연결
- [x] `InterviewReportControllerTest`, `InterviewReportServiceTest` 추가

### 보완 메모
- [ ] `docs/report.md`는 과거 스펙(세부 점수/강점/개선점)을 일부 포함하고 있어 최신 구현 기준으로 별도 정리 필요
- [ ] 전체 테스트 스위트 기준으로는 기존 `InterviewSequenceIntegrationTest` 실패 원인 점검이 추가로 필요

## 📋 요구사항 분석

`docs/api.md` (우선), `docs/report.md`, `HISTORY.md`, `summary.st`, `feedback-followup.st` 기준.

### GET /api/interviews/{interviewId}/report

- 인증 필요, 본인 면접만 접근 가능
- 세션이 COMPLETED 상태일 때만 조회 가능 (`SESSION_NOT_COMPLETED`)
- 리포트 미생성 시 `REPORT_NOT_FOUND`
- 응답 필드: `interviewId`, `interviewType`, `jobCategory`, `difficulty`, `questionCount`, `completedAt`, `totalScore`,
  `overallComment`, `questions[]`
- `questions[]`는 QUESTION 타입만, questionIndex 오름차순
- `QuestionReport` 필드: `questionId`, `questionIndex`, `questionContent`, `answerContent`, `feedbackContent`, `score`,
  `keywords`

### POST /api/interviews/{interviewId}/answers (변경)

- 응답에 `score` 필드 추가 (0~100)
- `score`는 `Feedback` 엔티티에 저장

### POST /api/interviews/{interviewId}/sessions/finish (변경)

- 세션 종료 시 리포트 1회 생성 및 DB 저장 (이미 구현 중)
- LLM은 `totalScore` + `overallComment` + `questions[]{questionId, keywords[]}` 만 산출
- `jobCategory`는 사용자 Profile에서 조회 (하드코딩 제거)
- 대화 컨텍스트는 ChatMemory advisor의 conversation_id만으로 주입 (수동 history 주입 제거)
- 이미 리포트가 존재하면 `REPORT_ALREADY_EXISTS` (이미 구현됨)

### 리포트 조립 규칙

- `questions[].score` = DB의 `Feedback.score` 조회 결과
- `questions[].keywords` = LLM 응답의 questions 배열에서 매칭 (`questionId` 기준)
- FOLLOW_UP 질문은 리포트 questions 배열에서 제외

---

## 🔍 현재 구현 상태

### 올바르게 구현된 것

- `InterviewReportController` — GET /report 엔드포인트, 경로·인증 구조 정상
- `InterviewReportService.getReport()` — 소유자 검증, 세션 완료 검증 로직 존재
- `InterviewReportManager.create()` — `REPORT_ALREADY_EXISTS` 중복 방지 존재
- `InterviewReportManager.find()` — `REPORT_NOT_FOUND` 예외 처리 존재
- `InterviewReportRepository` — `findByInterviewIdAndStatus`, `existsByInterviewIdAndStatus` 존재
- `InterviewSessionService.finish()` — `interviewReportService.generateReport(interviewId)` 호출 연결됨
- `ErrorCode` — `REPORT_NOT_FOUND`, `REPORT_ALREADY_EXISTS`, `SESSION_NOT_COMPLETED` 이미 존재
- `InterviewSessionEntity.completedAt` 필드 존재
- `ProfileFinder.findByUserId()` 존재

### 부분적으로 구현된 것

- `InterviewReportService.generateReport()` — 호출 구조는 있으나 구 스펙 기반. `InterviewSessionRepository` 직접 주입 (아키텍처 위반)
- `ClaudeReportGenerator.generate()` — 대화 히스토리를 수동 concat(`formatHistory`) 하면서 동시에 ChatMemory advisor도 주입 (이중 주입)
- `MockReportGenerator` — 구 스펙(`Scores`, `strengths`, `improvements`) 반환

---

## ⚠️ 차이점 및 버그

### 구 스펙 잔재 (전면 교체 필요)

| 대상                          | 현재                                                                                                                    | 변경 후                                                                                                           |
|-----------------------------|-----------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|
| `ScoreReport` 도메인           | `totalScore`, `DetailScores`, `strengths[]`, `improvements[]`, `overallComment`                                       | `totalScore`, `overallComment` 만                                                                               |
| `DetailScores` 도메인          | `conceptUnderstanding`, `problemSolving`, `communication`                                                             | 삭제                                                                                                             |
| `ReportQuestion` 도메인        | `questionId`, `question`, `answer`, `feedback`                                                                        | `questionId`, `questionIndex`, `questionContent`, `answerContent`, `feedbackContent`, `score`, `keywords[]` 추가 |
| `ReportGenerationResultDto` | `totalScore`, `Scores{...}`, `strengths[]`, `improvements[]`, `overallComment`                                        | `totalScore`, `overallComment`, `questions[]{questionId, keywords[]}`                                          |
| `InterviewReportEntity`     | `conceptUnderstanding`, `problemSolving`, `communication`, `strengthsJson`, `improvementsJson` 컬럼                     | `overallComment`, `questionsJson`(questionId→keywords 매핑 직렬화) 로 교체                                             |
| `InterviewReportResponse`   | `scoreReport{totalScore, scores, strengths, improvements, overallComment}`, `questions[{question, answer, feedback}]` | 최신 API 스펙 구조로 전면 교체                                                                                            |
| `InterviewReport` 도메인       | `ScoreReport`, `List<ReportQuestion>` 포함                                                                              | 상동 수정                                                                                                          |

### `score` 필드 누락 (신규 추가 필요)

| 대상                            | 누락 내용                                               |
|-------------------------------|-----------------------------------------------------|
| `Feedback` 도메인                | `score` 필드 없음                                       |
| `FeedbackEntity`              | `score` 컬럼 없음                                       |
| `FeedbackManager.create()`    | `score` 파라미터 없음                                     |
| `AnswerResultDto`             | `score` 필드 없음 (`feedback`, `followUpQuestion` 만 존재) |
| `AnswerResult` 도메인            | `score` 필드 없음 (`feedback` 만)                        |
| `MockAnswerResultGenerator`   | `score` 반환 없음                                       |
| `ClaudeAnswerResultGenerator` | JSON 파싱 시 `score` 매핑 없음                             |
| `AnswerService.answer()`      | `feedbackManager.create()` 호출 시 `score` 미전달         |
| `CreateAnswerResponse`        | `score` 필드 없음                                       |
| `AnswerControllerTest`        | `score` 응답 검증 없음                                    |

### 아키텍처 위반

| 파일                       | 위반                                                                                                               |
|--------------------------|------------------------------------------------------------------------------------------------------------------|
| `InterviewReportService` | `InterviewSessionRepository` 직접 주입. Service는 Repository를 직접 참조 불가. `InterviewSessionFinder` 또는 전용 Finder를 통해야 함. |
| `InterviewReportService` | `validateSessionCompleted()` 내부에서 `InterviewSessionEntity`(인프라 객체)를 직접 사용. Logic Layer 이하에서만 Entity 사용 가능.       |
| `InterviewReportService` | `generateReport()`에서 `InterviewSessionRepository` 직접 접근해 `InterviewSessionEntity` 사용.                            |

### 로직 오류 / 누락

| 대상                                   | 문제                                                                                                                          |
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `ClaudeReportGenerator`              | `formatHistory()`로 히스토리 문자열을 concat한 뒤 ChatMemory advisor도 같이 주입 → 대화 내용 이중 전달. `formatHistory` 및 `fullPrompt` 조합 전부 제거 필요. |
| `ClaudeReportGenerator`              | `jobCategory` 하드코딩(`"BACKEND"`). `Profile.jobCategory` 조회로 교체 필요.                                                           |
| `ReportQuestionReader`               | FOLLOW_UP 질문을 필터링하지 않음. QUESTION 타입만 포함하고 `questionIndex` 오름차순으로 정렬해야 함.                                                    |
| `InterviewReportService.getReport()` | `reportQuestionReader.read()` 결과를 그대로 사용하는데, 여기서 `score`와 `keywords`도 조립해야 함.                                               |
| `InterviewReportResponse`            | `interviewId`, `interviewType`, `jobCategory`, `difficulty`, `questionCount`, `completedAt` 필드 없음 (API 스펙과 불일치).            |

---

## 🗺️ 개발 계획

### Step 1: Feedback.score 추가 및 답변 제출 응답에 score 반영

**목적**: `POST /answers` 응답에 `score` 포함 + DB 저장.

- [ ] `Feedback` 도메인 — `score` 필드 추가. `create(Long answerId, String feedbackContent, int score)` 생성자/팩터리 수정
- [ ] `FeedbackEntity` — `score INT NOT NULL` 컬럼 추가. `from(Feedback)`, `toDomain()` 수정
- [ ] `FeedbackManager.create(Long answerId, String feedbackContent, int score)` — 시그니처에 `score` 추가
- [ ] `AnswerResultDto` — `score` 필드 추가 (`feedback`, `score`, `followUpQuestion`)
- [ ] `AnswerResult` 도메인 — `score` 필드 추가
- [ ] `MockAnswerResultGenerator` — `score` 고정값(80) 반환 추가
- [ ] `ClaudeAnswerResultGenerator.parseFeedbackResult()` — `score` Jackson 매핑 확인 (필드 이름이 일치하면 자동 매핑)
- [ ] `AnswerService.answer()` —
  `feedbackManager.create(answer.getId(), answerResultDto.feedback(), answerResultDto.score())` 로 수정.
  `AnswerResult.of(answerResultDto.feedback(), answerResultDto.score())` 반환
- [ ] `CreateAnswerResponse` — `score` 필드 추가. `of(AnswerResult)` 매핑 수정
- [ ] `AnswerControllerTest` — `score` 응답 필드 검증 추가

**커밋 단위**: `feat: add score field to feedback and answer response`

---

### Step 2: 도메인 객체 재설계 (구 스펙 제거)

**목적**: `InterviewReport`, `ReportQuestion`, `ScoreReport`, `DetailScores` 를 신규 스펙으로 교체.

- [ ] `DetailScores` — 삭제
- [ ] `ScoreReport` — `totalScore`, `overallComment` 만 남기도록 수정. `List<String> strengths`, `List<String> improvements`,
  `DetailScores scores` 제거
- [ ] `ReportQuestion` — `questionIndex`, `answerContent`→`answerContent`, `feedbackContent`, `score`, `keywords` 필드 추가.
  필드명 통일(`question`→`questionContent`, `answer`→`answerContent`, `feedback`→`feedbackContent`)
- [ ] `InterviewReport` — `ScoreReport` 제거하고 `totalScore`, `overallComment`를 직접 포함하거나 `ScoreReport` 유지(선택). 응답 조립에 필요한
  `interviewType`, `jobCategory`, `difficulty`, `questionCount`, `completedAt`을 포함하도록 수정

**커밋 단위**: `refactor: redesign report domain objects to new spec`

---

### Step 3: ReportGenerationResultDto 및 LLM Generator 재설계

**목적**: LLM 출력 스키마를 `summary.st` 와 정합.

- [ ] `ReportGenerationResultDto` — 전면 교체: `totalScore`, `overallComment`, `List<QuestionKeywords> questions`. 내부 레코드
  `QuestionKeywords(Long questionId, List<String> keywords)` 추가. 구 필드(`Scores`, `strengths`, `improvements`) 삭제
- [ ] `ReportGenerator` 인터페이스 — 시그니처 변경: `generate(String sessionId, Interview interview, String jobCategory)`.
  `List<SessionHistory>` 파라미터 제거
- [ ] `ClaudeReportGenerator.generate()` — `formatHistory()` 메서드 삭제. `fullPrompt` 문자열 concat 제거.
  `PromptTemplate.render()` 에 `interviewType`, `difficulty`, `jobCategory` 만 전달. advisor의 `CONVERSATION_ID` 단일 경로로 대화
  컨텍스트 주입
- [ ] `MockReportGenerator` — 신규 스키마(`totalScore`, `overallComment`, `questions[{questionId, keywords}]`) 로 교체. 고정
  `questionId` 는 임의값(테스트에서 주입된 ID와 무관하므로 통합 테스트에서 별도 처리)

**커밋 단위**: `refactor: update report generator to new LLM schema`

---

### Step 4: InterviewReportEntity 재설계

**목적**: DB 스키마를 신규 스펙에 맞게 변경.

- [ ] `InterviewReportEntity` — 삭제 컬럼: `conceptUnderstanding`, `problemSolving`, `communication`, `strengthsJson`,
  `improvementsJson`. 추가 컬럼: `questionsJson TEXT NOT NULL` (questionId→keywords 매핑, JSON 직렬화). `overallComment`는 유지
- [ ] `InterviewReportEntity.from(InterviewReport)` 정적 팩터리 재작성
- [ ] `InterviewReportEntity.toDomain()` 재작성 — `questions` 조립 로직은 매니저/서비스 계층에서 처리하므로 엔티티는 저장된 raw 데이터(totalScore,
  overallComment, questionsJson)만 노출
- [ ] DB 마이그레이션: `feat/report` 브랜치에서만 사용하므로 스키마 DROP-CREATE 방식으로 로컬 처리 (프로덕션 마이그레이션 스크립트 불필요, 단 팀 공유 시 주의)

**커밋 단위**: `refactor: redesign InterviewReportEntity schema`

---

### Step 5: 아키텍처 위반 수정 및 리포트 조립 로직 구현

**목적**: Service 레이어 아키텍처 위반 해소 + `questions[]` 최종 조립.

#### InterviewReportService 아키텍처 수정

- [ ] `InterviewSessionRepository` 직접 주입 제거. `InterviewSessionFinder`를 사용하도록 변경
- [ ] `validateSessionCompleted()` — `InterviewSessionEntity` 대신 도메인 객체 `InterviewSession`을 받아 `sessionStatus` 비교

#### generateReport() 재구현

```
1. InterviewFinder.find(interviewId) → Interview
2. InterviewSessionFinder.find(interviewId) → InterviewSession (sessionId 획득)
3. ProfileFinder.findByUserId(interview.getUserId()) → Profile (jobCategory 획득)
4. reportGenerator.generate(sessionId, interview, profile.getJobCategory().name())
5. LLM 응답: ReportGenerationResultDto { totalScore, overallComment, questions[{questionId, keywords}] }
6. FeedbackFinder(신규) 또는 FeedbackRepository 경유 FeedbackScoreFinder — 본 질문(QUESTION)의 answerId → Feedback.score 조회
7. SessionHistoryFinder.findSessionHistories(interviewId) → 본 질문(QUESTION) 필터 + questionIndex 오름차순 정렬
8. 조립: 각 QUESTION 히스토리 항목에 LLM keywords(questionId 매칭) + Feedback.score 결합 → List<ReportQuestion>
9. InterviewReport 생성 (interviewType, jobCategory, difficulty, questionCount, completedAt 포함)
10. InterviewReportManager.create(report)
```

#### getReport() 재구현

```
1. interviewSessionValidator.validateInterviewOwner(interviewId, userId)
2. InterviewSessionFinder.find(interviewId) → 세션 완료 여부 검증
3. InterviewFinder.find(interviewId) → 메타정보 획득
4. ProfileFinder.findByUserId(interview.getUserId()) → jobCategory
5. InterviewReportManager.find(interviewId) → 저장된 ReportGenerationResultDto 복원
6. SessionHistoryFinder + FeedbackScore 조립 → List<ReportQuestion>
7. InterviewReport 반환
```

#### FeedbackScoreFinder (신규 Logic 컴포넌트)

- [ ] `feedback/application/FeedbackScoreFinder` 생성
- 역할: `List<Long> answerIds` → `Map<Long, Integer>` (answerId → score) 반환
- `FeedbackRepository`를 통해 조회. `FeedbackEntity`에서 `score` 추출

#### ReportQuestionReader 수정

- [ ] FOLLOW_UP 타입 필터링 추가 (`questionType == QUESTION` 만)
- [ ] `questionIndex` 오름차순 정렬 보장
- [ ] `score`, `keywords` 조립 책임은 `InterviewReportService`에서 담당 (Reader는 히스토리만)

**커밋 단위**: `fix: fix architecture violation and redesign report assembly logic`

---

### Step 6: InterviewReportResponse 재설계 및 Controller 수정

**목적**: API 응답 스펙 정합.

- [ ] `InterviewReportResponse` 전면 재설계:
  ```java
  record InterviewReportResponse(
      Long interviewId,
      String interviewType,
      String jobCategory,
      String difficulty,
      int questionCount,
      String completedAt,         // ISO 8601
      int totalScore,
      String overallComment,
      List<QuestionReportResponse> questions
  )
  
  record QuestionReportResponse(
      Long questionId,
      int questionIndex,
      String questionContent,
      String answerContent,       // nullable
      String feedbackContent,     // nullable
      Integer score,              // nullable (미답변 시 null)
      List<String> keywords
  )
  ```
- [ ] `InterviewReportResponse.of(InterviewReport)` 정적 팩터리 구현
- [ ] 구 내부 레코드(`ScoreReportResponse`, `DetailScoresResponse`, `ReportQuestionResponse`) 삭제
- [ ] `InterviewReportController` — `InterviewReport` → `InterviewReportResponse.of(report)` 변환 확인

**커밋 단위**: `feat: implement report GET API response`

---

### Step 7: 통합 테스트 및 Controller 단위 테스트

- [ ] `InterviewReportControllerTest` (신규, WebMvcTest)
    - 정상 조회 200 + 응답 필드 검증
    - `SESSION_NOT_COMPLETED` 400 검증
    - `REPORT_NOT_FOUND` 404 검증
    - `INTERVIEW_ACCESS_DENIED` 403 검증

- [ ] `InterviewReportServiceTest` (신규, IntegrationTest 상속)
    - `generateReport()` — 세션 종료 후 리포트 생성 검증 (MockReportGenerator 사용)
    - `generateReport()` — 이미 리포트 존재 시 `REPORT_ALREADY_EXISTS` 예외
    - `getReport()` — 미완료 세션에서 `SESSION_NOT_COMPLETED` 예외
    - `getReport()` — 리포트 미존재 시 `REPORT_NOT_FOUND` 예외

- [ ] `InterviewSequenceIntegrationTest` 수정
    - 답변 응답에 `score` 필드 포함 여부 검증 추가

- [ ] `AnswerControllerTest` 수정
    - 답변 응답에 `score` 필드 검증 추가 (이미 존재하는 테스트에 assertion 추가)

**커밋 단위**: `test: add report and answer score tests`

---

## 📁 영향받는 파일

### 수정 (기존 파일)

```
feedback/domain/Feedback.java
feedback/infra/FeedbackEntity.java
feedback/application/FeedbackManager.java

answer/application/dto/AnswerResultDto.java
answer/domain/AnswerResult.java
answer/application/AnswerService.java
answer/infra/ClaudeAnswerResultGenerator.java
answer/infra/MockAnswerResultGenerator.java
answer/presentation/dto/CreateAnswerResponse.java

report/domain/ScoreReport.java
report/domain/ReportQuestion.java
report/domain/InterviewReport.java
report/application/dto/ReportGenerationResultDto.java
report/application/ReportGenerator.java         (인터페이스 시그니처)
report/application/InterviewReportService.java  (아키텍처 위반 수정 + 조립 로직)
report/application/ReportQuestionReader.java    (FOLLOW_UP 필터 + 정렬)
report/application/InterviewReportManager.java  (새 스키마 직렬화)
report/infra/ClaudeReportGenerator.java         (formatHistory 제거, jobCategory 수정)
report/infra/MockReportGenerator.java           (새 스키마)
report/infra/InterviewReportEntity.java         (컬럼 교체)
report/presentation/dto/InterviewReportResponse.java (전면 재설계)
```

### 생성 (신규 파일)

```
feedback/application/FeedbackScoreFinder.java
report/presentation/InterviewReportControllerTest.java  (테스트)
report/application/InterviewReportServiceTest.java      (테스트)
```

### 삭제

```
report/domain/DetailScores.java
```

---

## ⚡ 주의사항

1. **Service → Repository 직접 참조 금지**: `InterviewReportService`가 현재 `InterviewSessionRepository`를 직접 주입받고 있어 아키텍처 위반. 반드시
   `InterviewSessionFinder` 경유로 수정.

2. **Entity를 Service 계층에서 사용 금지**: `validateSessionCompleted()` 에서 `InterviewSessionEntity`를 직접 다루는 코드를 도메인 객체(
   `InterviewSession`) 기반으로 수정.

3. **이중 히스토리 주입 제거**: `ClaudeReportGenerator`의 `formatHistory()` + `fullPrompt` concat을 완전히 제거하고 ChatMemory advisor 단일
   경로만 유지. `summary.st`에 `{history}` 변수가 없음을 확인 완료.

4. **DB 스키마 변경**: `interview_reports` 테이블 컬럼이 대폭 변경됨. 로컬 개발 환경에서 기존 테이블을 DROP하거나 `spring.jpa.hibernate.ddl-auto=create`
   로 재생성 필요. 통합 테스트는 `IntegrationTest` 기반이므로 자동으로 처리됨.

5. **MockReportGenerator의 questionId 처리**: Mock에서 고정 questionId를 반환하면 실제 DB의 questionId와 매칭이 안 됨. 통합 테스트에서는 실제
   questionId를 넣거나 keywords 조립 테스트는 별도로 처리.

6. **FOLLOW_UP 필터링 위치**: LLM은 대화 컨텍스트에서 알아서 QUESTION만 results에 포함해야 하지만(`summary.st` 지시 있음), 백엔드 조립 단계에서도 반드시 QUESTION
   타입만 필터링하여 방어.

7. **score nullable 처리**: 미답변 질문의 경우 `Feedback`이 없어 `score`가 없음. `QuestionReportResponse.score`는 `Integer`(nullable)로
   선언.

8. **completedAt 타입**: `InterviewSessionEntity.completedAt`은 `LocalDateTime`. 응답에서는 ISO 8601 문자열로 직렬화 (
   `LocalDateTime.toString()` 또는 `DateTimeFormatter`).

---

## 🧪 테스트 명령

```bash
# 전체 테스트
./gradlew test

# 리포트 도메인만
./gradlew test --tests "wlsh.project.intervai.report.*"

# 답변 관련
./gradlew test --tests "wlsh.project.intervai.answer.*"

# 통합 시퀀스 테스트
./gradlew test --tests "wlsh.project.intervai.interview.integration.InterviewSequenceIntegrationTest"

# 세션 서비스 테스트
./gradlew test --tests "wlsh.project.intervai.session.application.InterviewSessionServiceTest"
```
