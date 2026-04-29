# 히스토리 페이지 구현 계획 — fe/feat/history (Issue #21)

## 브랜치
`fe/feat/history`

## 요구사항

- 면접 히스토리 목록 조회 (서버 페이지네이션: page, size)
- 인터뷰 타입 필터: CS / PORTFOLIO / ALL (서버 파라미터)
- 세션 상태 필터: COMPLETED / IN_PROGRESS (서버 파라미터)
- 키워드 검색 (클라이언트 사이드 — 백엔드 파라미터 미제공)
- 히스토리 카드: 인터뷰 타입 뱃지, 난이도 뱃지, 날짜, 문항 수, AI 스코어, 상태 뱃지
- AI SCORE: 백엔드 미지원 → "-- /100" 고정 표시 (향후 별도 이슈)
- 완료 세션: "결과 보기 >" 버튼 → `/interviews/:id/result` 이동
- 진행중 세션: "이어하기" 버튼 (disabled, 별도 이슈)
- 삭제: 모든 세션 삭제 가능 (DELETE /api/interviews/:id, 확인 다이얼로그 포함)
- 페이지네이션 UI (◁ 1 2 3 ... N ▷)

## 디자인 (history.png 기준)

레이아웃:
- 상단: "면접 히스토리" 타이틀 + 설명 텍스트
- 검색/필터 바: 검색 Input + 인터뷰 타입 Select + 상태 Select
- 카드 목록
- 하단 페이지네이션

색상 토큰:
- `CS` 뱃지: `bg-blue-100 text-blue-700` (레이블: "CS INTERVIEW")
- `PORTFOLIO` 뱃지: `bg-purple-100 text-purple-700` (레이블: "PORTFOLIO")
- `ALL` 뱃지: `bg-green-100 text-green-700` (레이블: "ALL ROUNDER")
- COMPLETED 상태: `text-green-600` (● 완료)
- IN_PROGRESS 상태: `text-orange-500` (● 진행중)
- "이어하기" 버튼: `bg-blue-600 text-white` (disabled)
- "결과 보기 >" 버튼: shadcn `variant="outline"`
- 삭제 아이콘: `text-gray-400 hover:text-red-500`

난이도 뱃지: ENTRY / JUNIOR / SENIOR

## 현재 구현 상태

- `front/src/shared/pages/HistoryPage.tsx` — 플레이스홀더만 존재
- `front/src/app/router.tsx` — `/history` 라우트 이미 등록됨
- `front/src/features/` — auth, dashboard, interview, profile 피처 존재
- `front/src/shared/api/httpClient.ts` — axios 인스턴스 재사용
- `front/src/shared/components/ui/` — shadcn/ui 프리미티브 재사용

## 백엔드 API (docs/api.md)

### GET /api/interviews
- Query: `page` (default 0), `size` (default 10), `interviewType` (선택: CS/PORTFOLIO/ALL), `sessionStatus` (선택: IN_PROGRESS/COMPLETED)
- Response: `{ content: InterviewSummary[], totalElements, totalPages, last }`
- InterviewSummary: `{ id, interviewType, difficulty, questionCount, sessionStatus, createdAt }`

### DELETE /api/interviews/{interviewId}
- 204 No Content
- 403: INTERVIEW_ACCESS_DENIED

## 파일 목록

| 파일 | 작업 | 설명 |
|------|------|------|
| `front/src/features/history/types/index.ts` | 생성 | 타입 정의 |
| `front/src/shared/types/queryKeys.ts` | 수정 | history 키 그룹 추가 |
| `front/src/features/history/api/historyApi.ts` | 생성 | API 함수 |
| `front/src/features/history/hooks/useInterviewHistory.ts` | 생성 | useQuery 훅 |
| `front/src/features/history/hooks/useDeleteInterview.ts` | 생성 | useMutation 훅 |
| `front/src/features/history/utils/formatDate.ts` | 생성 | 날짜 포맷 유틸 |
| `front/src/shared/components/ui/PaginationBar.tsx` | 생성 | 공유 페이지네이션 |
| `front/src/features/history/components/InterviewHistoryCard.tsx` | 생성 | 히스토리 카드 |
| `front/src/features/history/components/HistoryFilterBar.tsx` | 생성 | 검색+필터 바 |
| `front/src/features/history/components/DeleteConfirmDialog.tsx` | 생성 | 삭제 확인 다이얼로그 |
| `front/src/features/history/pages/HistoryPage.tsx` | 생성 | 히스토리 페이지 구현 |
| `front/src/shared/pages/HistoryPage.tsx` | 수정 | features/history re-export로 교체 |

## 개발 계획

### Step 1: 타입 정의
파일: `front/src/features/history/types/index.ts`

```ts
export type InterviewType = 'CS' | 'PORTFOLIO' | 'ALL';
export type SessionStatus = 'COMPLETED' | 'IN_PROGRESS';
export type Difficulty = 'ENTRY' | 'JUNIOR' | 'SENIOR';

export interface InterviewSummary {
  id: number;
  interviewType: InterviewType;
  difficulty: Difficulty;
  questionCount: number;
  sessionStatus: SessionStatus;
  createdAt: string; // ISO 8601
}

export interface InterviewListResponse {
  content: InterviewSummary[];
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface InterviewListParams {
  page?: number;
  size?: number;
  interviewType?: InterviewType;
  sessionStatus?: SessionStatus;
}

export interface HistoryFilterState {
  keyword: string;
  interviewType: InterviewType | '';
  sessionStatus: SessionStatus | '';
}
```

### Step 2: Query Key 등록
파일: `front/src/shared/types/queryKeys.ts` (수정)

기존 queryKeys 객체에 history 그룹 추가:
```ts
history: {
  all: ['history'] as const,
  list: (params?: InterviewListParams) => ['history', 'list', params] as const,
},
```

### Step 3: API 클라이언트
파일: `front/src/features/history/api/historyApi.ts`

```ts
import { httpClient } from '@/shared/api/httpClient';
import type { InterviewListParams, InterviewListResponse } from '../types';

export const getInterviewList = (params: InterviewListParams): Promise<InterviewListResponse> =>
  httpClient.get('/api/interviews', { params }).then((res) => res.data);

export const deleteInterview = (interviewId: number): Promise<void> =>
  httpClient.delete(`/api/interviews/${interviewId}`).then(() => undefined);
```

### Step 4: TanStack Query 훅

파일: `front/src/features/history/hooks/useInterviewHistory.ts`
- `useQuery` + `placeholderData: (prev) => prev` (TanStack Query v5 방식)
- queryKey: `queryKeys.history.list(params)`

파일: `front/src/features/history/hooks/useDeleteInterview.ts`
- `useMutation`
- onSuccess: `queryClient.invalidateQueries({ queryKey: queryKeys.history.all })`

### Step 5: UI 컴포넌트

#### PaginationBar (공유)
파일: `front/src/shared/components/ui/PaginationBar.tsx`
- Props: `currentPage: number` (0-based), `totalPages: number`, `onPageChange: (page: number) => void`
- 1-based 페이지 번호 표시, 최대 5개 + 말줄임표
- 이전/다음 버튼 경계에서 disabled
- shadcn/ui Button 재사용

#### InterviewHistoryCard
파일: `front/src/features/history/components/InterviewHistoryCard.tsx`
- Props: `interview: InterviewSummary`, `onDelete: (id: number) => void`
- 인터뷰 타입 뱃지 색상 매핑 (CS→blue, PORTFOLIO→purple, ALL→green)
- 날짜: `createdAt` → `YYYY.MM.DD` 포맷
- AI SCORE: 항상 "-- /100"
- 상태 뱃지: COMPLETED="● 완료"(green), IN_PROGRESS="● 진행중"(orange)
- 완료: "결과 보기 >" 버튼 → navigate('/interviews/{id}/result')
- 진행중: "이어하기" 버튼 (disabled)
- 삭제 아이콘 (lucide-react Trash2) → onDelete(id) — 모든 상태에 표시

#### HistoryFilterBar
파일: `front/src/features/history/components/HistoryFilterBar.tsx`
- Props: `filters: HistoryFilterState`, `onFilterChange: (filters: HistoryFilterState) => void`
- 검색 Input (placeholder: "면접 키워드 검색...")
- 인터뷰 타입 Select: "전체 면접" / "CS INTERVIEW" / "PORTFOLIO" / "ALL ROUNDER"
- 상태 Select: "전체 상태" / "완료" / "진행중"

#### DeleteConfirmDialog
파일: `front/src/features/history/components/DeleteConfirmDialog.tsx`
- shadcn/ui Dialog 재사용
- 확인: variant="destructive", isPending 시 "삭제 중..."

### Step 6: 페이지 컴포넌트

파일: `front/src/features/history/pages/HistoryPage.tsx`
- `useSearchParams`로 page URL 파라미터 관리 (기본값 0)
- 서버: interviewType, sessionStatus 필터 파라미터로 전달
- 클라이언트: keyword로 content 배열 필터링
- 로딩: Skeleton 카드 표시
- 에러: 에러 메시지 표시
- 빈 목록: "면접 기록이 없습니다." 안내
- 삭제 플로우: 아이콘 → Dialog → 확인 → mutate → invalidate → Dialog 닫기
- 필터 변경 시 page=0으로 리셋

파일: `front/src/shared/pages/HistoryPage.tsx` (수정)
```tsx
export { default } from '@/features/history/pages/HistoryPage';
```

### Step 7: 검증
```bash
cd /tmp/intervai-fe/front && npm run typecheck
cd /tmp/intervai-fe/front && npm run build
```

## 주의사항

1. **InterviewType enum**: API는 `CS`, `PORTFOLIO`, `ALL` — `CS_INTERVIEW`, `ALL_ROUNDER` 아님
2. **크로스 피처 임포트 금지**: features/history에서 다른 features 직접 임포트 불가
3. **서버 상태 Zustand 저장 금지**: TanStack Query로만 관리
4. **TanStack Query v5**: `keepPreviousData` 없음, `placeholderData: (prev) => prev`
5. **AI SCORE 고정**: "-- /100" 하드코딩
6. **"이어하기" 버튼**: disabled + cursor-not-allowed
7. **삭제는 모든 상태 허용**: COMPLETED, IN_PROGRESS 모두 삭제 가능 (디자인 기준)
8. **queryKeys.history** 타입 임포트: InterviewListParams를 queryKeys.ts에서 임포트할 때 순환 참조 주의 — 필요시 params를 unknown으로 타입 완화

---

# [구 플랜 — 면접 진행(채팅) 화면 구현 플랜]
<!-- 아래는 이전 이슈(면접 세션 채팅 화면)의 플랜입니다. 참고용으로 유지합니다. -->

## 요구사항 (구 플랜)

- phase='chat' 진입 시 채팅 형식의 면접 진행 화면을 표시한다

## 요구사항

- phase='chat' 진입 시 채팅 형식의 면접 진행 화면을 표시한다
- `GET /api/interviews/{interviewId}/questions/current`로 현재 질문을 조회한다
- 사용자가 답변을 입력하고 제출하면 `POST /api/interviews/{interviewId}/answers`를 호출한다
- 답변 제출 후 반환된 피드백은 기본 숨김 처리하고, "피드백 보기" 클릭 시 노출한다
- 답변 제출 후 다시 현재 질문을 조회하여 꼬리 질문 또는 다음 본 질문을 채팅에 추가한다
- AI 메시지 버블은 왼쪽, CANDIDATE 답변 버블은 오른쪽 정렬로 표시한다
- 꼬리 질문은 `FOLLOW-UP 꼬리 질문` 배지를 함께 표시한다
- 상단 헤더에 면접 세션 제목, "면접 종료" 버튼, 질문 진행 카운터를 표시한다
- "면접 종료" 클릭 시 확인 다이얼로그 표시 후 `POST /api/interviews/{interviewId}/sessions/finish` 호출한다
- `hasNext: false`인 경우 "모든 질문이 완료되었습니다" 안내 UI를 표시하고 사용자가 세션 종료를 요청한다
- `SESSION_ALREADY_COMPLETED` (400) 수신 시 결과(finished) 화면으로 이동한다
- `ALL_QUESTIONS_ANSWERED` (400) 수신 시 모든 질문 완료로 처리하여 finish 안내를 표시한다
- LLM 호출 중(`isPending: true`)에는 전송 버튼을 disabled 처리하고 스피너를 표시한다
- phase='finished' 진입 시 면접 완료 안내를 표시한다

---

## 디자인 요약 (Stitch 스크린: d09b31c7131f40af8e52cf532e06d74e)

**색상 토큰**

| 토큰 | 값 |
|------|----|
| Primary | `#4648d4` |
| Secondary | `#6b38d4` |
| Tertiary | `#00885d` |
| Background | `#faf8ff` |
| Surface Container | `#eaedff` |
| On-Surface (텍스트) | `#131b2e` |
| Outline | `#767586` |
| 피드백 카드 border | `4px solid #00885d` |

**폰트**: Inter (sans-serif)

**레이아웃 구성**

- 전체: AppLayout 사이드바(기존) + 오른쪽 메인 영역 (flex-col)
- 메인 영역: 헤더(고정) + 채팅 스크롤 영역(flex-1) + 입력 영역(하단 고정)

**컴포넌트별 스타일**

- 헤더: 배경 `#eaedff`, 제목 `#131b2e`, "면접 종료" 버튼 outline 스타일 (`border-[#4648d4] text-[#4648d4]`), 진행 카운터 배지 (`bg-[#eaedff] text-[#4648d4]`)
- AI 버블: 왼쪽 정렬, 배경 `#eaedff`, 라벨 "INTERVAI AI" (`text-[#4648d4]`)
- CANDIDATE 버블: 오른쪽 정렬, 배경 `#4648d4`, 텍스트 white, 라벨 "CANDIDATE" (`text-[#767586]`)
- 꼬리 질문 배지: `bg-[#6b38d4] text-white rounded-full px-3 py-0.5 text-xs`
- 피드백 카드: `border-l-4 border-[#00885d] bg-white`, "피드백 보기" 토글 버튼, 점수(있을 경우) 표시
- 입력 영역: textarea + 전송 버튼 (`bg-[#4648d4]`), 하단 보조 버튼 "포트폴리오 참고", "이전 질문 보기"
- 스크롤바: `6px`, track `#eaedff`
- Border radius: 기본 `rounded`, lg `rounded-lg`, xl `rounded-xl`, full `rounded-full`

**텍스트**

- 헤더 제목: "AI 면접 세션" (면접 유형 정보 prefix 추가: "CS 지식 - 프론트엔드 AI 면접 세션")
- 면접 종료 버튼: "면접 종료"
- 진행 카운터: "질문 {n}/{total}"
- AI 라벨: "INTERVAI AI"
- 사용자 라벨: "CANDIDATE"
- 꼬리 질문 배지: "FOLLOW-UP 꼬리 질문"
- 피드백 토글: "실시간 피드백 보기" / "피드백 숨기기"
- 입력 placeholder: "답변을 입력하세요..."
- 종료 확인 메시지: "종료하면 현재까지의 면접만 저장됩니다. 종료하시겠습니까?"
- 모든 질문 완료 안내: "모든 질문이 완료되었습니다. 면접을 종료해주세요."

---

## 현재 구현 상태

**이미 구현된 항목**

| 파일 | 상태 | 내용 |
|------|------|------|
| `features/interview/api/interviewApi.ts` | 완료 | createInterview, createSession, createQuestions |
| `features/interview/stores/interviewStore.ts` | 완료 | interviewId, sessionId, phase 관리 |
| `features/interview/hooks/useCreateInterview.ts` | 완료 | setup→generating→chat phase 전환 |
| `features/interview/pages/InterviewPage.tsx` | 부분 | setup/generating 표시, chat/finished 미구현 |
| `features/interview/components/InterviewSetupForm.tsx` | 완료 | 면접 설정 폼 |
| `shared/api/constants.ts` | 부분 | currentQuestion, answers, finish 경로 없음 |
| `shared/types/queryKeys.ts` | 부분 | currentQuestion 키 정의되어 있음, answers/finish 없음 |
| `shared/api/apiError.ts` | 완료 | 에러 코드 메시지 이미 정의됨 |
| `shared/types/enums.ts` | 완료 | QuestionType 포함 모든 enum 정의됨 |

**구현이 필요한 항목**

- `interviewApi.ts`에 `getCurrentQuestion`, `submitAnswer`, `finishSession` API 함수 추가
- `constants.ts`에 `currentQuestion`, `answers`, `finish` 경로 추가
- `useCurrentQuestion` hook (useQuery, phase='chat'에서만 enabled)
- `useSubmitAnswer` hook (useMutation, 답변 제출 후 질문 재조회 트리거)
- `useFinishSession` hook (useMutation)
- `ChatHeader` 컴포넌트
- `AiMessageBubble` 컴포넌트
- `CandidateMessageBubble` 컴포넌트
- `FeedbackCard` 컴포넌트 (접힘/펼침 토글)
- `ChatMessageList` 컴포넌트 (채팅 메시지 목록 + 자동 스크롤)
- `ChatInputArea` 컴포넌트 (textarea + 전송 버튼)
- `FinishConfirmDialog` 컴포넌트 (면접 종료 확인 다이얼로그)
- `AllQuestionsCompletedBanner` 컴포넌트
- `InterviewChatScreen` 컴포넌트 (채팅 화면 최상위)
- `InterviewFinishedScreen` 컴포넌트
- `InterviewPage.tsx` 수정 — phase='chat'과 'finished' 분기에 실제 컴포넌트 렌더링

---

## 상태 관리 전략

### 로컬 상태 (컴포넌트 내부 useState)

채팅 메시지 목록은 서버에 저장하지 않고 클라이언트 세션 내에서만 유지하므로, `ChatMessage` 배열을 `InterviewChatScreen` 내부 useState로 관리한다.

```typescript
interface ChatMessage {
  id: string                          // nanoid 또는 Date.now() 문자열
  role: 'ai' | 'candidate'
  content: string
  questionType?: QuestionType         // AI 메시지에만 사용 (꼬리 질문 배지 표시용)
  feedback?: string                   // candidate 메시지 직후 연결된 피드백
  isFeedbackOpen?: boolean            // 피드백 접힘/펼침 상태
  questionId?: number                 // AI 메시지에만 사용 (답변 제출 시 필요)
}
```

- 질문 조회 성공 시 AI 메시지 추가
- 답변 제출 시 CANDIDATE 메시지 추가
- 답변 제출 성공 시 마지막 CANDIDATE 메시지에 feedback 연결
- 피드백 토글 상태는 메시지별로 독립 관리

### 서버 상태 (TanStack Query)

- `useCurrentQuestion`: `queryKeys.interview.currentQuestion(interviewId)` 키 사용, `enabled: phase === 'chat' && interviewId !== null`, `staleTime: Infinity` (수동 refetch로만 갱신)
- `useSubmitAnswer`: useMutation, onSuccess 시 `queryClient.invalidateQueries({ queryKey: queryKeys.interview.currentQuestion(interviewId) })` 호출
- `useFinishSession`: useMutation

### Zustand 스토어 확장

현재 `interviewStore`에 `questionCount`(총 질문 수)와 `currentQuestionIndex`(현재 진행 번호) 저장을 추가한다. 이 두 값은 서버 상태가 아닌 UI 표시용 카운터이므로 Zustand에 보관한다.

```typescript
// interviewStore.ts 추가 필드
questionCount: number | null
currentQuestionIndex: number        // 1부터 시작 (표시용)
setQuestionCount: (count: number) => void
incrementQuestionIndex: () => void
```

`questionCount`는 setup 완료 시 `CreateInterviewResponse.questionCount`에서 읽어 저장한다. `currentQuestionIndex`는 새 AI 메시지가 추가될 때마다 증가한다.

---

## 구현 순서

### Step 1: API 경로 및 타입 추가

- [ ] `shared/api/constants.ts` — `currentQuestion`, `answers`, `finish` 경로 추가
- [ ] `features/interview/api/interviewApi.ts` — `getCurrentQuestion`, `submitAnswer`, `finishSession` 함수 및 타입 추가

### Step 2: Zustand 스토어 확장

- [ ] `features/interview/stores/interviewStore.ts` — `questionCount`, `currentQuestionIndex`, `setQuestionCount`, `incrementQuestionIndex` 추가
- [ ] `features/interview/hooks/useCreateInterview.ts` — onSuccess에서 `setQuestionCount(interview.questionCount)` 호출 추가

### Step 3: TanStack Query 훅 추가

- [ ] `features/interview/hooks/useCurrentQuestion.ts` — useQuery, `enabled: phase === 'chat' && !!interviewId`
- [ ] `features/interview/hooks/useSubmitAnswer.ts` — useMutation, onSuccess 시 queryInvalidate + 메시지 콜백
- [ ] `features/interview/hooks/useFinishSession.ts` — useMutation, onSuccess 시 `setPhase('finished')`, `SESSION_ALREADY_COMPLETED` 에러도 동일 처리

### Step 4: 채팅 UI 컴포넌트 구현

- [ ] `features/interview/components/ChatHeader.tsx` — 헤더 (제목 + 종료 버튼 + 진행 카운터)
- [ ] `features/interview/components/FinishConfirmDialog.tsx` — 종료 확인 다이얼로그
- [ ] `features/interview/components/AiMessageBubble.tsx` — AI 메시지 버블 (꼬리 질문 배지 포함)
- [ ] `features/interview/components/FeedbackCard.tsx` — 피드백 섹션 (접힘/펼침 토글)
- [ ] `features/interview/components/CandidateMessageBubble.tsx` — CANDIDATE 답변 버블 + FeedbackCard 조합
- [ ] `features/interview/components/ChatMessageList.tsx` — 메시지 목록 렌더링 + 자동 스크롤 (useRef + useEffect)
- [ ] `features/interview/components/AllQuestionsCompletedBanner.tsx` — 완료 안내 + 면접 종료 버튼
- [ ] `features/interview/components/ChatInputArea.tsx` — textarea + 전송 버튼 (isPending disabled 처리)

### Step 5: 채팅 화면 조립

- [ ] `features/interview/components/InterviewChatScreen.tsx` — 채팅 화면 최상위 (메시지 상태, 훅 조합)
- [ ] `features/interview/components/InterviewFinishedScreen.tsx` — 면접 완료 화면 (resetInterview 호출 + 대시보드 이동 유도)

### Step 6: 페이지 연결

- [ ] `features/interview/pages/InterviewPage.tsx` — phase='chat' → `<InterviewChatScreen />`, phase='finished' → `<InterviewFinishedScreen />` 렌더링

---

## 생성/수정 파일 목록

| 파일 | 작업 | 설명 |
|------|------|------|
| `front/src/shared/api/constants.ts` | 수정 | currentQuestion, answers, finish 경로 추가 |
| `front/src/features/interview/api/interviewApi.ts` | 수정 | getCurrentQuestion, submitAnswer, finishSession 함수 및 타입 추가 |
| `front/src/features/interview/stores/interviewStore.ts` | 수정 | questionCount, currentQuestionIndex 상태 추가 |
| `front/src/features/interview/hooks/useCreateInterview.ts` | 수정 | onSuccess에서 setQuestionCount 호출 추가 |
| `front/src/features/interview/hooks/useCurrentQuestion.ts` | 생성 | 현재 질문 조회 useQuery 훅 |
| `front/src/features/interview/hooks/useSubmitAnswer.ts` | 생성 | 답변 제출 useMutation 훅 |
| `front/src/features/interview/hooks/useFinishSession.ts` | 생성 | 세션 종료 useMutation 훅 |
| `front/src/features/interview/components/ChatHeader.tsx` | 생성 | 채팅 상단 헤더 |
| `front/src/features/interview/components/FinishConfirmDialog.tsx` | 생성 | 면접 종료 확인 다이얼로그 |
| `front/src/features/interview/components/AiMessageBubble.tsx` | 생성 | AI 질문 버블 |
| `front/src/features/interview/components/FeedbackCard.tsx` | 생성 | 피드백 토글 카드 |
| `front/src/features/interview/components/CandidateMessageBubble.tsx` | 생성 | 사용자 답변 버블 + 피드백 |
| `front/src/features/interview/components/ChatMessageList.tsx` | 생성 | 채팅 메시지 목록 + 자동 스크롤 |
| `front/src/features/interview/components/AllQuestionsCompletedBanner.tsx` | 생성 | 전체 질문 완료 안내 배너 |
| `front/src/features/interview/components/ChatInputArea.tsx` | 생성 | 답변 입력 영역 |
| `front/src/features/interview/components/InterviewChatScreen.tsx` | 생성 | 채팅 화면 최상위 컴포넌트 |
| `front/src/features/interview/components/InterviewFinishedScreen.tsx` | 생성 | 면접 완료 화면 |
| `front/src/features/interview/pages/InterviewPage.tsx` | 수정 | chat/finished 분기에 실제 컴포넌트 연결 |

---

## 컴포넌트 트리

```
InterviewPage (phase 분기)
├── InterviewSetupForm          (phase='setup', 기존)
├── GeneratingScreen            (phase='generating', 기존 인라인 → 별도 분리 불필요)
├── InterviewChatScreen         (phase='chat', 신규)
│   ├── ChatHeader
│   │   └── FinishConfirmDialog
│   ├── ChatMessageList
│   │   ├── AiMessageBubble     (role='ai', questionType로 꼬리 질문 배지)
│   │   ├── CandidateMessageBubble (role='candidate')
│   │   │   └── FeedbackCard   (feedback 있을 때)
│   │   └── AllQuestionsCompletedBanner (hasNext=false이고 아직 finish 전)
│   └── ChatInputArea
└── InterviewFinishedScreen     (phase='finished', 신규)
```

---

## 데이터 흐름 상세

```
[phase='chat' 진입]
  ↓
useCurrentQuestion (enabled=true)
  → 성공: AI 메시지 추가, currentQuestionIndex 증가
  → ALL_QUESTIONS_ANSWERED (400): 완료 배너 표시
  → SESSION_ALREADY_COMPLETED (400): setPhase('finished')

[사용자 답변 입력 → 전송]
  ↓
useSubmitAnswer.mutate({ questionId, content })
  → isPending: 전송 버튼 disabled + 스피너
  → 성공:
      1. CANDIDATE 메시지 추가 (content + feedback)
      2. queryClient.invalidateQueries(currentQuestion 키)
      → useCurrentQuestion 자동 재실행
          → 새 AI 메시지 추가
          → hasNext=false: AllQuestionsCompletedBanner 표시
  → ANSWER_ALREADY_EXISTS (409): 토스트 에러

[면접 종료 버튼 클릭]
  ↓
FinishConfirmDialog 열림
  → 확인:
      useFinishSession.mutate(interviewId)
      → 성공: setPhase('finished')
      → SESSION_ALREADY_COMPLETED (400): setPhase('finished') (동일 처리)
      → 기타 에러: 토스트
  → 취소: 다이얼로그 닫힘
```

---

## 에러 처리 매핑

| 에러 코드 | 발생 API | 처리 방법 |
|-----------|---------|----------|
| `SESSION_ALREADY_COMPLETED` | currentQuestion / finishSession | `setPhase('finished')` |
| `ALL_QUESTIONS_ANSWERED` | currentQuestion | `AllQuestionsCompletedBanner` 표시 (finish 유도) |
| `ANSWER_ALREADY_EXISTS` | submitAnswer | 토스트 에러 (`shared/api/apiError.ts` 메시지 사용) |
| `INTERVIEW_ACCESS_DENIED` | 모든 API | 토스트 에러 |
| `INTERVIEW_NOT_FOUND` | 모든 API | 토스트 에러 |
| LLM 타임아웃 등 기타 | submitAnswer | "AI 응답이 지연되고 있습니다" 토스트 |

`SESSION_ALREADY_COMPLETED`와 `ALL_QUESTIONS_ANSWERED`는 `useCurrentQuestion`의 `onError`, `useFinishSession`의 `onError` 콜백에서 `extractApiError(error).code`로 분기 처리한다.

---

## 주의사항

1. **서버 상태 Zustand 저장 금지**: `currentQuestion` 응답 데이터(questionId, question, questionType, hasNext)는 TanStack Query 캐시에만 유지한다. 단, `questionCount`와 `currentQuestionIndex`는 서버에서 제공하지 않는 UI 표시 전용 카운터이므로 Zustand에 보관하는 것이 유일한 예외다.

2. **enabled 옵션 필수**: `useCurrentQuestion`은 `phase === 'chat' && interviewId !== null`일 때만 활성화한다. 조기 호출 시 `SESSION_NOT_FOUND` 에러가 발생한다.

3. **staleTime 설정**: `useCurrentQuestion`은 `staleTime: Infinity`로 설정하여 자동 refetch를 방지한다. 답변 제출 성공 시에만 `invalidateQueries`로 수동 갱신한다.

4. **API_PATHS 상수 활용**: 직접 URL 문자열 사용 금지. 모든 경로는 `shared/api/constants.ts`의 `API_PATHS`를 통해 참조한다.

5. **cross-feature import 금지**: `features/interview/` 내부에서 `features/auth/` 등 다른 feature 직접 import 금지. `interviewStore`에서 interviewId를 읽고, auth 정보가 필요하면 `shared/`로 이동시킨다.

6. **queryKeys 패턴 준수**: `shared/types/queryKeys.ts`에 정의된 `queryKeys.interview.currentQuestion(interviewId)` 키를 그대로 사용한다. invalidateQueries 시에도 동일한 키 함수를 사용한다.

7. **자동 스크롤**: `ChatMessageList`에서 `useRef<HTMLDivElement>`로 최하단 요소를 참조하고, 메시지 목록이 변경될 때마다 `useEffect`로 `scrollIntoView`를 호출한다.

8. **hasNext 처리 시점**: `useSubmitAnswer` onSuccess 후 `invalidateQueries`로 갱신된 질문의 `hasNext`를 확인하는 것이 아니라, 갱신된 `useCurrentQuestion` 결과에서 확인한다. `InterviewChatScreen`에서 `hasNext` 값을 감지하여 `AllQuestionsCompletedBanner` 표시 여부를 결정한다.

---

## 검증 방법

- `npm run typecheck` — TypeScript 오류 없음
- `npm run lint` — ESLint 오류 없음

**수동 확인 시나리오**

1. setup 완료 후 phase='chat'으로 전환되면 첫 번째 AI 질문이 표시되는지 확인
2. 답변 입력 후 전송 시 LLM 처리 중 버튼 disabled + 스피너가 표시되는지 확인
3. 피드백이 기본 숨김 처리되고, "피드백 보기" 클릭 시 노출되는지 확인
4. 꼬리 질문 응답 시 `FOLLOW-UP 꼬리 질문` 배지가 표시되는지 확인
5. 진행 카운터가 새 질문마다 증가하는지 확인
6. "면접 종료" 버튼 클릭 시 확인 다이얼로그가 표시되는지 확인
7. 다이얼로그에서 취소 클릭 시 채팅 화면 유지 확인
8. 다이얼로그에서 확인 클릭 시 phase='finished' 전환 확인
9. 모든 질문 완료 후 `AllQuestionsCompletedBanner`가 표시되고 종료 버튼이 활성화되는지 확인
10. `SESSION_ALREADY_COMPLETED` 에러 수신 시 자동으로 finished 화면으로 이동하는지 확인
