# Interview Sequence HTTPie

`INTERVIEW_SEQUENCE.md` 기준 면접 흐름을 수동 또는 반자동으로 검증하기 위한 `httpie` 자산입니다.

## 디렉터리 선택

이 파일들은 `tools/httpie/interview-sequence/`에 보관합니다.

- `tools/`: 애플리케이션 코드와 분리된 운영/검증용 자산 위치
- `httpie/`: 도구 목적이 바로 드러남
- `interview-sequence/`: 면접 플로우 검증 파일만 묶어 관리 가능

## 전제 조건

- 서버 실행 중
- `http` 설치됨
- `jq` 설치됨

## 빠른 실행

1. 환경 파일 준비

```bash
cp tools/httpie/interview-sequence/env.example tools/httpie/interview-sequence/.env
```

2. 필요 값 수정

- `BASE_URL`
- `TEST_NICKNAME`
- `TEST_PASSWORD`
- `INTERVIEW_TYPE`
- `DIFFICULTY`
- `QUESTION_COUNT`
- `INTERVIEWER_TONE`
- `MAX_QUESTION_ROUNDS`

3. 전체 시퀀스 실행

```bash
bash tools/httpie/interview-sequence/run-sequence.sh
```

## 개별 실행

각 단계는 순차 실행을 전제로 하며, 앞 단계 결과를 `tmp/`에 저장합니다.

```bash
bash tools/httpie/interview-sequence/01-sign-up.sh
bash tools/httpie/interview-sequence/02-login.sh
bash tools/httpie/interview-sequence/03-create-interview.sh
bash tools/httpie/interview-sequence/04-create-session.sh
bash tools/httpie/interview-sequence/05-create-questions.sh
bash tools/httpie/interview-sequence/06-get-current-question.sh
bash tools/httpie/interview-sequence/07-answer-current-question.sh
bash tools/httpie/interview-sequence/08-finish-session.sh
```

`run-sequence.sh`는 `06 -> 07`을 반복하다가, `06`에서 받은 현재 질문의 `hasNext`가 `false`인 질문에 대한 답변이 완료되면 그 시점에만 `08`을 실행합니다.

무한 반복 방지를 위해 최대 반복 횟수는 `.env`의 `MAX_QUESTION_ROUNDS`로 제한합니다. 기본 예시는 `60`이며, 현재 요구 범위인 4~50개 질문을 충분히 커버합니다.

## 검증 포인트

- 회원가입 또는 로그인으로 `accessToken` 획득
- 면접 생성 후 `id` 반환
- 세션 생성 후 `sessionId` 반환
- 질문 생성 후 `questions[]` 반환
- 현재 질문 조회 시 `questionId`, `questionType`, `hasNext` 확인
- 답변 제출 시 `201 Created`와 `feedback` 반환
- 마지막 질문의 답변 완료 직후에만 세션 종료 호출
- 세션 종료 시 `200 OK`와 빈 body 반환

## 구현 기준 메모

- 실제 구현은 종료 경로를 두 개 모두 지원합니다.
  - 문서 기준: `/api/interviews/{interviewId}/sessions/finish`
  - 호환 경로: `/api/interviews/{interviewId}/finish`
- 면접 생성 요청은 현재 구현 기준으로 `techStacks`를 받지 않습니다. 이 시나리오는 실제 컨트롤러 스펙에 맞춰 작성했습니다.
