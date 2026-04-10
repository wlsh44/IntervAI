# API 문서

IntervAI 백엔드 REST API 레퍼런스입니다.

**Base URL**: `http://localhost:8080`

---

## 공통 사항

### 인증

인증이 필요한 API는 요청 헤더에 Access Token을 포함해야 합니다.

```
Authorization: Bearer {accessToken}
```

Refresh Token은 HttpOnly 쿠키(`refreshToken`)로 관리됩니다.

### 에러 응답 형식

```json
{
  "code": "ERROR_CODE",
  "message": "에러 메시지"
}
```

### 유효성 검사 실패 (400)

Bean Validation 실패 시 아래 형식으로 반환됩니다.

```json
{
  "code": "INVALID_INPUT",
  "message": "잘못된 입력값입니다."
}
```

---

## Enum 타입 정의

### InterviewType

| 값 | 설명 |
|----|------|
| `CS` | CS 기초 질문 |
| `PORTFOLIO` | 포트폴리오 기반 질문 |
| `ALL` | CS + 포트폴리오 종합 |

### Difficulty

| 값 | 설명 |
|----|------|
| `ENTRY` | 신입 |
| `JUNIOR` | 주니어 |
| `SENIOR` | 시니어 |

### InterviewerTone

| 값 | 설명 |
|----|------|
| `FRIENDLY` | 친절한 면접관 |
| `NORMAL` | 일반적인 면접관 |
| `AGGRESSIVE` | 압박적인 면접관 |

### CsCategory

| 값 | 설명 |
|----|------|
| `DATA_STRUCTURE` | 자료구조 |
| `ALGORITHM` | 알고리즘 |
| `NETWORK` | 네트워크 |
| `LANGUAGE` | 언어 |
| `DATABASE` | 데이터베이스 |

### QuestionType

| 값 | 설명 |
|----|------|
| `QUESTION` | 본 질문 |
| `FOLLOW_UP` | 꼬리 질문 |

### JobCategory

| 값 | 설명 |
|----|------|
| `FRONTEND` | 프론트엔드 |
| `BACKEND` | 백엔드 |
| `FULLSTACK` | 풀스택 |
| `ANDROID` | 안드로이드 |
| `IOS` | iOS |
| `DEVOPS` | DevOps |
| `DATA_ENGINEER` | 데이터 엔지니어 |
| `ML_ENGINEER` | ML 엔지니어 |

### CareerLevel

| 값 | 설명 |
|----|------|
| `ENTRY` | 신입 |
| `JUNIOR` | 주니어 |
| `SENIOR` | 시니어 |

### SessionStatus

| 값 | 설명 |
|----|------|
| `IN_PROGRESS` | 진행 중 |
| `COMPLETED` | 완료 |

---

## 사용자 인증

### 회원가입

```
POST /api/users/sign-up
```

**인증**: 불필요

**Request Body**

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `nickname` | String | O | 4~8자 |
| `password` | String | O | 4~12자 |

**Response** `201 Created`

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 사용자 ID |
| `nickname` | String | 닉네임 |
| `accessToken` | String | JWT Access Token (24시간) |

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `DUPLICATE_NICKNAME` | 400 | 이미 사용 중인 닉네임 |
| `INVALID_INPUT` | 400 | 유효성 검사 실패 |

**예시**

```json
// Request
{
  "nickname": "tester",
  "password": "pass1234"
}

// Response 201
{
  "id": 1,
  "nickname": "tester",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 로그인

```
POST /api/users/login
```

**인증**: 불필요

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `nickname` | String | O | 닉네임 |
| `password` | String | O | 비밀번호 |

**Response** `200 OK`

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 사용자 ID |
| `nickname` | String | 닉네임 |
| `accessToken` | String | JWT Access Token (24시간) |

> Refresh Token은 HttpOnly 쿠키(`refreshToken`, 7일)로 자동 설정됩니다.

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `LOGIN_FAILED` | 401 | 닉네임 또는 비밀번호 불일치 |
| `INVALID_INPUT` | 400 | 유효성 검사 실패 |

**예시**

```json
// Request
{
  "nickname": "tester",
  "password": "pass1234"
}

// Response 200
{
  "id": 1,
  "nickname": "tester",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Access Token 갱신

```
POST /api/auth/refresh
```

**인증**: 불필요 (Refresh Token 쿠키 필요)

**Request**: 쿠키에 `refreshToken` 포함 (자동)

**Response** `200 OK`

| 필드 | 타입 | 설명 |
|------|------|------|
| `accessToken` | String | 새 JWT Access Token |

> 새 Refresh Token이 HttpOnly 쿠키로 재설정됩니다 (Rotation).

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `REFRESH_TOKEN_NOT_FOUND` | 401 | Refresh Token 쿠키 없음 |
| `INVALID_REFRESH_TOKEN` | 401 | 유효하지 않은 Refresh Token |
| `EXPIRED_TOKEN` | 401 | 만료된 토큰 |

**예시**

```json
// Response 200
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## 프로필

### 프로필 생성

```
POST /api/users/profile
```

**인증**: 필요 (JWT의 userId 사용)

**Request Body**: 없음 (userId는 인증 토큰에서 추출)

**Response** `201 Created`

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 프로필 ID |
| `jobCategory` | JobCategory | 희망 직군 (초기값 null) |
| `careerLevel` | CareerLevel | 경력 수준 (초기값 null) |
| `techStacks` | String[] | 기술 스택 목록 (초기값 []) |
| `portfolioLinks` | String[] | 포트폴리오 링크 목록 (초기값 []) |

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `PROFILE_ALREADY_EXISTS` | 409 | 이미 프로필 존재 |

---

### 프로필 조회

```
GET /api/users/profile
```

**인증**: 필요 (JWT의 userId로 본인 프로필 조회)

**Path Parameters**: 없음

**Response** `200 OK`

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 프로필 ID |
| `jobCategory` | JobCategory | 희망 직군 |
| `careerLevel` | CareerLevel | 경력 수준 |
| `techStacks` | String[] | 기술 스택 목록 |
| `portfolioLinks` | String[] | 포트폴리오 링크 목록 |
| `updatedAt` | String (ISO 8601) | 마지막 수정 시각 |

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `PROFILE_NOT_FOUND` | 404 | 프로필 없음 |

**예시**

```json
// Response 200
{
  "id": 1,
  "jobCategory": "BACKEND",
  "careerLevel": "JUNIOR",
  "techStacks": ["Java", "Spring Boot", "MySQL"],
  "portfolioLinks": ["https://github.com/user/project"],
  "updatedAt": "2026-04-10T12:34:56"
}
```

---

### 프로필 수정

```
PUT /api/users/profile
```

**인증**: 필요 (JWT의 userId로 본인 프로필 수정)

**Path Parameters**: 없음

**Request Body**

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `jobCategory` | JobCategory | O | Enum 값 |
| `careerLevel` | CareerLevel | O | Enum 값 |
| `techStacks` | String[] | O | 1~20개, 각 항목 공백 불가 |
| `portfolioLinks` | String[] | X | 최대 5개, 각 항목 공백 불가 |

**Response** `200 OK`

`ProfileResponse` (프로필 조회와 동일 구조)

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `PROFILE_NOT_FOUND` | 404 | 프로필 없음 |
| `INVALID_INPUT` | 400 | 유효성 검사 실패 |

**예시**

```json
// Request
{
  "jobCategory": "BACKEND",
  "careerLevel": "JUNIOR",
  "techStacks": ["Java", "Spring Boot", "MySQL"],
  "portfolioLinks": ["https://github.com/user/project"]
}

// Response 200
{
  "id": 1,
  "jobCategory": "BACKEND",
  "careerLevel": "JUNIOR",
  "techStacks": ["Java", "Spring Boot", "MySQL"],
  "portfolioLinks": ["https://github.com/user/project"]
}
```

---

## 면접

### 면접 설정 생성

```
POST /api/interviews
```

**인증**: 필요

**Request Body**

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `interviewType` | InterviewType | O | `CS` / `PORTFOLIO` / `ALL` |
| `difficulty` | Difficulty | O | `ENTRY` / `JUNIOR` / `SENIOR` |
| `questionCount` | Integer | O | 5~10 |
| `interviewerTone` | InterviewerTone | O | `FRIENDLY` / `NORMAL` / `AGGRESSIVE` |
| `csSubjects` | CsSubject[] | 조건부 | `interviewType`이 `CS` 또는 `ALL`인 경우 필수 |
| `portfolioLinks` | String[] | 조건부 | `interviewType`이 `PORTFOLIO` 또는 `ALL`인 경우 필수 |

**CsSubject 구조**

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| `category` | CsCategory | O | Enum 값 |
| `topics` | String[] | O | 1개 이상, 각 항목 공백 불가 |

**Response** `201 Created`

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 면접 ID |
| `interviewType` | InterviewType | 면접 유형 |
| `difficulty` | Difficulty | 난이도 |
| `questionCount` | Integer | 질문 수 |
| `interviewerTone` | InterviewerTone | 면접관 톤 |
| `csSubjects` | CsSubjectResponse[] | CS 과목 목록 |
| `portfolioLinks` | String[] | 포트폴리오 링크 목록 |

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INVALID_QUESTION_COUNT` | 400 | 질문 수 범위 초과 (5~10) |
| `CS_SUBJECT_REQUIRED` | 400 | CS/ALL 유형에 csSubjects 미입력 |
| `PORTFOLIO_LINK_REQUIRED` | 400 | PORTFOLIO/ALL 유형에 portfolioLinks 미입력 |

**예시**

```json
// Request (CS 유형)
{
  "interviewType": "CS",
  "difficulty": "JUNIOR",
  "questionCount": 5,
  "interviewerTone": "NORMAL",
  "csSubjects": [
    {
      "category": "NETWORK",
      "topics": ["HTTP", "TCP/IP"]
    },
    {
      "category": "DATABASE",
      "topics": ["인덱스", "트랜잭션"]
    }
  ]
}

// Response 201
{
  "id": 1,
  "interviewType": "CS",
  "difficulty": "JUNIOR",
  "questionCount": 5,
  "interviewerTone": "NORMAL",
  "csSubjects": [
    { "category": "NETWORK", "topics": ["HTTP", "TCP/IP"] },
    { "category": "DATABASE", "topics": ["인덱스", "트랜잭션"] }
  ],
  "portfolioLinks": []
}
```

---

### 면접 세션 생성

```
POST /api/interviews/{interviewId}/sessions
```

**인증**: 필요 (본인 면접만 가능)

**Path Parameters**

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `interviewId` | Long | 면접 ID |

**Response** `201 Created`

| 필드 | 타입 | 설명 |
|------|------|------|
| `sessionId` | Long | 생성된 세션 ID |

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INTERVIEW_NOT_FOUND` | 404 | 면접 없음 |
| `INTERVIEW_ACCESS_DENIED` | 403 | 타인 면접 접근 |

**예시**

```json
// Response 201
{
  "sessionId": 1
}
```

---

### 질문 일괄 생성

```
POST /api/interviews/{interviewId}/questions
```

**인증**: 필요 (본인 면접만 가능)

LLM에 질문 생성을 요청하고 DB에 저장합니다. 세션 생성 후 반드시 1회 호출해야 합니다.

> LLM 호출로 인해 응답까지 수 초가 소요될 수 있습니다.

**Path Parameters**

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `interviewId` | Long | 면접 ID |

**Response** `201 Created`

| 필드 | 타입 | 설명 |
|------|------|------|
| `questions` | QuestionItem[] | 생성된 질문 목록 |

**QuestionItem 구조**

| 필드 | 타입 | 설명 |
|------|------|------|
| `questionId` | Long | 질문 ID |
| `content` | String | 질문 내용 |
| `questionIndex` | Integer | 질문 순서 (0부터 시작) |

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INTERVIEW_NOT_FOUND` | 404 | 면접 없음 |
| `INTERVIEW_ACCESS_DENIED` | 403 | 타인 면접 접근 |
| `SESSION_NOT_FOUND` | 404 | 세션 없음 (세션 생성 먼저 필요) |
| `SESSION_ALREADY_COMPLETED` | 400 | 종료된 세션 |
| `QUESTION_COUNT_EXCEEDED` | 400 | 이미 질문이 생성된 경우 |

**예시**

```json
// Response 201
{
  "questions": [
    { "questionId": 1, "content": "HTTP와 HTTPS의 차이점을 설명해주세요.", "questionIndex": 0 },
    { "questionId": 2, "content": "TCP와 UDP의 차이점은 무엇인가요?", "questionIndex": 1 }
  ]
}
```

---

### 현재 질문 조회

```
GET /api/interviews/{interviewId}/questions/current
```

**인증**: 필요 (본인 면접만 가능)

현재 답변해야 할 질문을 반환합니다. 본 질문 또는 꼬리 질문일 수 있습니다.

**Path Parameters**

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `interviewId` | Long | 면접 ID |

**Response** `200 OK`

| 필드 | 타입 | 설명 |
|------|------|------|
| `questionId` | Long | 질문 ID |
| `question` | String | 질문 내용 |
| `questionType` | QuestionType | `QUESTION` / `FOLLOW_UP` |
| `hasNext` | Boolean | 다음 질문 존재 여부 |

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INTERVIEW_NOT_FOUND` | 404 | 면접 없음 |
| `INTERVIEW_ACCESS_DENIED` | 403 | 타인 면접 접근 |
| `SESSION_NOT_FOUND` | 404 | 세션 없음 |
| `SESSION_ALREADY_COMPLETED` | 400 | 종료된 세션 |
| `ALL_QUESTIONS_ANSWERED` | 400 | 모든 질문 완료 |

**예시**

```json
// Response 200
{
  "questionId": 1,
  "question": "HTTP와 HTTPS의 차이점을 설명해주세요.",
  "questionType": "QUESTION",
  "hasNext": true
}
```

---

### 답변 제출

```
POST /api/interviews/{interviewId}/answers
```

**인증**: 필요

답변을 제출하면 LLM이 피드백과 꼬리 질문을 생성합니다.
- 꼬리 질문이 생성된 경우: 다음 `GET /questions/current`에서 꼬리 질문 반환
- 꼬리 질문이 없거나 최대 수(3개) 초과: 자동으로 다음 본 질문으로 이동

> LLM 호출로 인해 응답까지 수 초가 소요될 수 있습니다.

**Path Parameters**

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `interviewId` | Long | 면접 ID |

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `questionId` | Long | O | 현재 질문 ID |
| `content` | String | O | 답변 내용 (공백 불가) |

**Response** `201 Created`

| 필드 | 타입 | 설명 |
|------|------|------|
| `feedback` | String | LLM 생성 피드백 |

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INTERVIEW_NOT_FOUND` | 404 | 면접 없음 |
| `INTERVIEW_ACCESS_DENIED` | 403 | 타인 면접 접근 |
| `QUESTION_NOT_FOUND` | 404 | 질문 없음 |
| `ANSWER_ALREADY_EXISTS` | 409 | 이미 답변한 질문 |
| `SESSION_ALREADY_COMPLETED` | 400 | 종료된 세션 |

**예시**

```json
// Request
{
  "questionId": 1,
  "content": "HTTPS는 HTTP에 SSL/TLS 암호화를 추가한 프로토콜입니다. 데이터를 암호화하여 중간자 공격을 방지합니다."
}

// Response 201
{
  "feedback": "핵심 개념인 암호화를 잘 설명하셨습니다. 추가로 인증서(Certificate)의 역할과 443 포트 사용에 대해서도 언급하면 더 완성도 있는 답변이 됩니다."
}
```

---

### 세션 종료

```
POST /api/interviews/{interviewId}/sessions/finish
```

**인증**: 필요 (본인 면접만 가능)

면접 세션을 종료하고 상태를 COMPLETED로 변경한다. 세션이 이미 완료된 경우 에러를 반환한다.

**Path Parameters**

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `interviewId` | Long | 면접 ID |

**Response** `200 OK` (body 없음)

**에러**

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INTERVIEW_NOT_FOUND` | 404 | 면접 없음 |
| `INTERVIEW_ACCESS_DENIED` | 403 | 타인 면접 접근 |
| `SESSION_NOT_FOUND` | 404 | 세션 없음 |
| `SESSION_ALREADY_COMPLETED` | 400 | 이미 종료된 세션 |

**예시**

```
// Response 200 (body 없음)
```

---

## 면접 목록 조회

### 면접 목록 조회

```
GET /api/interviews
```

**인증**: 필요

**Query Parameters**

| 파라미터 | 타입 | 기본값 | 설명 |
|---------|------|--------|------|
| `page` | Integer | 0 | 페이지 번호 (0부터 시작) |
| `size` | Integer | 10 | 페이지 크기 |

**Response** `200 OK`

| 필드 | 타입 | 설명 |
|------|------|------|
| `content` | InterviewSummary[] | 면접 요약 목록 |
| `totalElements` | Long | 전체 면접 수 |
| `totalPages` | Integer | 전체 페이지 수 |
| `last` | Boolean | 마지막 페이지 여부 |

**InterviewSummary 구조**

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | 면접 ID |
| `interviewType` | InterviewType | 면접 유형 |
| `difficulty` | Difficulty | 난이도 |
| `questionCount` | Integer | 질문 수 |
| `sessionStatus` | SessionStatus | 세션 상태 (`IN_PROGRESS` / `COMPLETED`) |
| `createdAt` | String (ISO 8601) | 면접 생성 시각 |

**에러**: 없음 (인증된 사용자의 면접이 없으면 빈 목록 반환)

**예시**

```json
// Response 200
{
  "content": [
    {
      "id": 1,
      "interviewType": "CS",
      "difficulty": "JUNIOR",
      "questionCount": 5,
      "sessionStatus": "COMPLETED",
      "createdAt": "2026-04-10T12:34:56"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

---

## 에러 코드 레퍼런스

### 인증 (4xx)

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INVALID_TOKEN` | 401 | 올바르지 않은 토큰 |
| `EXPIRED_TOKEN` | 401 | 만료된 토큰 |
| `INVALID_REFRESH_TOKEN` | 401 | 유효하지 않은 Refresh Token |
| `REFRESH_TOKEN_NOT_FOUND` | 401 | Refresh Token 쿠키 없음 |
| `LOGIN_FAILED` | 401 | 닉네임/비밀번호 불일치 |

### 사용자

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `DUPLICATE_NICKNAME` | 400 | 중복 닉네임 |

### 프로필

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `PROFILE_NOT_FOUND` | 404 | 프로필 없음 |
| `PROFILE_ACCESS_DENIED` | 403 | 타인 프로필 접근 |
| `PROFILE_ALREADY_EXISTS` | 409 | 프로필 중복 생성 |

### 면접

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `INTERVIEW_NOT_FOUND` | 404 | 면접 없음 |
| `INTERVIEW_ACCESS_DENIED` | 403 | 타인 면접 접근 |
| `INVALID_QUESTION_COUNT` | 400 | 질문 수 5~10 범위 초과 |
| `CS_SUBJECT_REQUIRED` | 400 | CS 유형에 csSubjects 미입력 |
| `PORTFOLIO_LINK_REQUIRED` | 400 | PORTFOLIO 유형에 portfolioLinks 미입력 |

### 세션

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `SESSION_NOT_FOUND` | 404 | 세션 없음 |
| `SESSION_ALREADY_COMPLETED` | 400 | 종료된 세션 |
| `SESSION_ACCESS_DENIED` | 401 | 접근 불가 세션 |
| `SESSION_NOT_COMPLETED` | 400 | 미완료 세션 (리포트 생성 시) |

### 질문 / 답변

| ErrorCode | HTTP | 설명 |
|-----------|------|------|
| `QUESTION_NOT_FOUND` | 404 | 질문 없음 |
| `QUESTION_COUNT_EXCEEDED` | 400 | 질문 수 초과 |
| `ALL_QUESTIONS_ANSWERED` | 400 | 모든 질문 완료 |
| `QUESTION_NOT_YET_ANSWERED` | 400 | 현재 질문 미답변 |
| `ANSWER_ALREADY_EXISTS` | 409 | 이미 답변 존재 |
| `ANSWER_NOT_FOUND` | 404 | 답변 없음 |
| `FOLLOW_UP_LIMIT_EXCEEDED` | 400 | 꼬리 질문 최대 3개 초과 |

---

## 전체 면접 플로우 예시

```
1. POST /api/interviews                          → interviewId 획득
2. POST /api/interviews/{interviewId}/sessions   → sessionId 획득
3. POST /api/interviews/{interviewId}/questions  → 질문 일괄 생성 (LLM)
4. GET  /api/interviews/{interviewId}/questions/current  → 첫 번째 질문 수신
5. POST /api/interviews/{interviewId}/answers    → 답변 제출 + 피드백 수신
   └─ 꼬리 질문이 있으면 → 4번으로 이동 (questionType: FOLLOW_UP)
   └─ 없으면 → 4번으로 이동 (questionType: QUESTION, 다음 본 질문)
6. hasNext: false가 될 때까지 4~5번 반복
7. POST /api/interviews/{interviewId}/sessions/finish  → 세션 종료 (status: COMPLETED)
```