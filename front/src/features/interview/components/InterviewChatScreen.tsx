import { useCallback, useEffect, useRef, useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { Loader2 } from 'lucide-react'
import { useInterviewStore } from '../stores/interviewStore'
import { useSubmitAnswer } from '../hooks/useSubmitAnswer'
import { useFinishSession } from '../hooks/useFinishSession'
import { ApiErrorCode, extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/toastStore'
import { queryKeys } from '../../../shared/types/queryKeys'
import { QuestionType } from '../../../shared/types/enums'
import { getCurrentQuestion, getSessionHistory } from '../api/interviewApi'
import ChatHeader from './ChatHeader'
import ChatMessageList from './ChatMessageList'
import ChatInputArea from './ChatInputArea'
import AllQuestionsCompletedBanner from './AllQuestionsCompletedBanner'
import FinishConfirmDialog from './FinishConfirmDialog'
import type { ChatMessage } from '../types/chat'

const InterviewChatScreen = () => {
  const {
    interviewId,
    questionCount,
    currentQuestionIndex,
    setPhase,
    incrementQuestionIndex,
    setCurrentQuestionIndex,
  } = useInterviewStore()
  const queryClient = useQueryClient()
  const { toast } = useToast()

  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [allCompleted, setAllCompleted] = useState(false)
  const [pendingQuestionId, setPendingQuestionId] = useState<number | null>(null)
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [isLoadingHistory, setIsLoadingHistory] = useState(true)
  // 다음 질문 로드 실패 시 재시도 버튼 표시용
  const [nextQuestionFetchFailed, setNextQuestionFetchFailed] = useState(false)
  // P3: ref로 중복 처리 방지 (React StrictMode에서 effect가 두 번 실행되어도 안전)
  const appendedQuestionIds = useRef(new Set<number>())
  // hasNext: false인 질문(마지막 질문)에 답변하면 /questions/current 대신 /sessions/finish 호출
  const isLastQuestion = useRef(false)

  const appendAiQuestion = useCallback(
    (currentInterviewId: number) => {
      setNextQuestionFetchFailed(false)
      isLastQuestion.current = false
      queryClient
        .fetchQuery({
          queryKey: queryKeys.interview.currentQuestion(currentInterviewId),
          queryFn: () => getCurrentQuestion(currentInterviewId),
          staleTime: 0,
        })
        .then((question) => {
          // P3: 이미 처리한 질문이면 스킵 (StrictMode 이중 실행 방어)
          if (appendedQuestionIds.current.has(question.questionId)) return
          appendedQuestionIds.current.add(question.questionId)

          const aiMsg: ChatMessage = {
            id: `ai-${question.questionId}`,
            role: 'ai',
            content: question.question,
            questionType: question.questionType,
            questionId: question.questionId,
          }
          setMessages((prev) => [...prev, aiMsg])
          setPendingQuestionId(question.questionId)
          // 본 질문일 때만 카운터 증가 (꼬리 질문은 questionCount에 포함되지 않음)
          if (question.questionType === QuestionType.QUESTION) {
            incrementQuestionIndex()
          }
          // hasNext: false → 마지막 질문. 답변 후 /questions/current 대신 /sessions/finish 호출
          isLastQuestion.current = !question.hasNext
        })
        .catch((err: unknown) => {
          const apiError = extractApiError(err)
          if (apiError.code === ApiErrorCode.SESSION_ALREADY_COMPLETED) {
            setPhase('finished')
          } else if (apiError.code === ApiErrorCode.ALL_QUESTIONS_ANSWERED) {
            setAllCompleted(true)
          } else {
            // P1: 그 외 에러(네트워크 오류, INTERVIEW_NOT_FOUND 등) — 토스트 + 재시도 버튼 표시
            toast(getErrorMessage(apiError.code), 'error')
            setNextQuestionFetchFailed(true)
          }
        })
    },
    [queryClient, incrementQuestionIndex, setPhase, toast],
  )

  useEffect(() => {
    if (interviewId === null) return

    let cancelled = false

    // interviewId 변경 시 상태 초기화
    setMessages([])
    setAllCompleted(false)
    setNextQuestionFetchFailed(false)
    appendedQuestionIds.current.clear()
    isLastQuestion.current = false
    setIsLoadingHistory(true)

    getSessionHistory(interviewId)
      .then((history) => {
        if (cancelled) return

        const restoredMessages: ChatMessage[] = []
        let mainQuestionCount = 0
        let hasUnanswered = false

        for (const item of history) {
          if (item.answerId === null) {
            hasUnanswered = true
            continue // 미답변 — appendAiQuestion이 처리
          }

          appendedQuestionIds.current.add(item.questionId)
          restoredMessages.push({
            id: `ai-${item.questionId}`,
            role: 'ai',
            content: item.questionContent,
            questionType: item.questionType,
            questionId: item.questionId,
          })
          restoredMessages.push({
            id: `candidate-${item.answerId}`,
            role: 'candidate',
            content: item.answerContent!,
            feedback: item.feedbackContent ?? undefined,
            isFeedbackOpen: false,
          })
          if (item.questionType === QuestionType.QUESTION) {
            mainQuestionCount++
          }
        }

        if (restoredMessages.length > 0) setMessages(restoredMessages)
        if (mainQuestionCount > 0) setCurrentQuestionIndex(mainQuestionCount)
        setIsLoadingHistory(false)

        // 미답변 질문이 있거나 히스토리가 없으면 현재 질문 fetch
        // 모두 답변됐으면 세션 미종료 상태 → 완료 배너 표시
        if (history.length === 0 || hasUnanswered) {
          appendAiQuestion(interviewId)
        } else {
          setAllCompleted(true)
        }
      })
      .catch(() => {
        if (cancelled) return
        setIsLoadingHistory(false)
        appendAiQuestion(interviewId)
      })

    return () => {
      cancelled = true
    }
  }, [interviewId, appendAiQuestion, setCurrentQuestionIndex])

  const { mutate: finish, isPending: isFinishing } = useFinishSession()

  const { mutate: submit, isPending: isSubmitting } = useSubmitAnswer({
    interviewId,
    onSuccess: (data, content) => {
      const candidateMsg: ChatMessage = {
        id: crypto.randomUUID?.() ?? `candidate-${Date.now()}`,
        role: 'candidate',
        content,
        feedback: data.feedback,
        isFeedbackOpen: false,
      }
      setMessages((prev) => [...prev, candidateMsg])
      setPendingQuestionId(null)

      if (interviewId !== null) {
        if (isLastQuestion.current) {
          // 마지막 질문 답변 완료 → 완료 배너 표시 (피드백 확인 후 사용자가 종료)
          setAllCompleted(true)
        } else {
          appendAiQuestion(interviewId)
        }
      }
    },
  })

  const handleToggleFeedback = (messageId: string) => {
    setMessages((prev) =>
      prev.map((msg) =>
        msg.id === messageId ? { ...msg, isFeedbackOpen: !msg.isFeedbackOpen } : msg,
      ),
    )
  }

  const handleSubmit = (content: string) => {
    if (pendingQuestionId === null) return
    submit({ questionId: pendingQuestionId, content })
  }

  const handleConfirmFinish = () => {
    setIsDialogOpen(false)
    if (interviewId === null) return
    finish(interviewId)
  }

  if (isLoadingHistory) {
    return (
      <div className="flex flex-col items-center justify-center flex-1 gap-3">
        <Loader2 size={36} className="animate-spin text-[#4648d4]" />
        <p className="text-sm text-[#767586]">이전 대화 내역을 불러오는 중...</p>
      </div>
    )
  }

  return (
    <div className="flex flex-col h-full">
      <ChatHeader
        currentQuestionIndex={currentQuestionIndex}
        questionCount={questionCount}
        onOpenDialog={() => setIsDialogOpen(true)}
        isFinishing={isFinishing}
      />

      <div className="flex flex-col flex-1 overflow-hidden">
        <ChatMessageList messages={messages} onToggleFeedback={handleToggleFeedback} />

        {allCompleted && (
          <div className="px-4 pb-4">
            <AllQuestionsCompletedBanner
              onOpenDialog={() => setIsDialogOpen(true)}
              isFinishing={isFinishing}
            />
          </div>
        )}

        {!allCompleted && nextQuestionFetchFailed && (
          <div className="px-4 pb-3 flex justify-center">
            <button
              onClick={() => interviewId !== null && appendAiQuestion(interviewId)}
              className="text-sm text-[#4648d4] border border-[#4648d4] rounded px-4 py-2 hover:bg-[#eaedff] transition-colors"
            >
              다음 질문 다시 불러오기
            </button>
          </div>
        )}

        {!allCompleted && !nextQuestionFetchFailed && (
          <ChatInputArea
            onSubmit={handleSubmit}
            isPending={isSubmitting}
            disabled={pendingQuestionId === null}
          />
        )}
      </div>

      <FinishConfirmDialog
        isOpen={isDialogOpen}
        onConfirm={handleConfirmFinish}
        onCancel={() => setIsDialogOpen(false)}
      />
    </div>
  )
}

export default InterviewChatScreen
