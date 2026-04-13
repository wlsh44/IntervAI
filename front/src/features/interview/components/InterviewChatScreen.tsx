import { useCallback, useEffect, useRef, useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { useInterviewStore } from '../stores/interviewStore'
import { useSubmitAnswer } from '../hooks/useSubmitAnswer'
import { useFinishSession } from '../hooks/useFinishSession'
import { ApiErrorCode, extractApiError } from '../../../shared/api/apiError'
import { queryKeys } from '../../../shared/types/queryKeys'
import { getCurrentQuestion } from '../api/interviewApi'
import ChatHeader from './ChatHeader'
import ChatMessageList from './ChatMessageList'
import ChatInputArea from './ChatInputArea'
import AllQuestionsCompletedBanner from './AllQuestionsCompletedBanner'
import { QuestionType } from '../../../shared/types/enums'
import type { ChatMessage } from '../types/chat'

const InterviewChatScreen = () => {
  const { interviewId, questionCount, currentQuestionIndex, setPhase, incrementQuestionIndex } =
    useInterviewStore()
  const queryClient = useQueryClient()

  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [allCompleted, setAllCompleted] = useState(false)
  const [pendingQuestionId, setPendingQuestionId] = useState<number | null>(null)
  // P3: ref로 중복 처리 방지 (React StrictMode에서 effect가 두 번 실행되어도 안전)
  const appendedQuestionIds = useRef(new Set<number>())

  const appendAiQuestion = useCallback(
    (currentInterviewId: number) => {
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
          // P2: 본 질문일 때만 카운터 증가 (꼬리 질문은 questionCount에 포함되지 않음)
          if (question.questionType === QuestionType.QUESTION) {
            incrementQuestionIndex()
          }
          // P1: hasNext: false는 "마지막 질문"을 의미하지 "완료"를 의미하지 않음.
          //     allCompleted는 답변 제출 후 ALL_QUESTIONS_ANSWERED 에러 시에만 세팅.
        })
        .catch((err: unknown) => {
          const apiError = extractApiError(err)
          if (apiError.code === ApiErrorCode.SESSION_ALREADY_COMPLETED) {
            setPhase('finished')
          } else if (apiError.code === ApiErrorCode.ALL_QUESTIONS_ANSWERED) {
            setAllCompleted(true)
          }
        })
    },
    [queryClient, incrementQuestionIndex, setPhase],
  )

  useEffect(() => {
    if (interviewId === null) return
    appendAiQuestion(interviewId)
    // 마운트 시 한 번만 실행 — interviewId는 chat phase 진입 시 고정됨
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const { mutate: finish, isPending: isFinishing } = useFinishSession()

  const { mutate: submit, isPending: isSubmitting } = useSubmitAnswer({
    interviewId,
    onSuccess: (data, content) => {
      const candidateMsg: ChatMessage = {
        id: crypto.randomUUID(),
        role: 'candidate',
        content,
        feedback: data.feedback,
        isFeedbackOpen: false,
      }
      setMessages((prev) => [...prev, candidateMsg])
      setPendingQuestionId(null)

      if (interviewId !== null) {
        appendAiQuestion(interviewId)
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

  const handleFinish = () => {
    if (interviewId === null) return
    finish(interviewId)
  }

  return (
    <div className="flex flex-col h-full">
      <ChatHeader
        currentQuestionIndex={currentQuestionIndex}
        questionCount={questionCount}
        onFinish={handleFinish}
        isFinishing={isFinishing}
      />

      <div className="flex flex-col flex-1 overflow-hidden">
        <ChatMessageList messages={messages} onToggleFeedback={handleToggleFeedback} />

        {allCompleted && (
          <div className="px-4 pb-4">
            <AllQuestionsCompletedBanner onFinish={handleFinish} isFinishing={isFinishing} />
          </div>
        )}

        {!allCompleted && (
          <ChatInputArea
            onSubmit={handleSubmit}
            isPending={isSubmitting}
            disabled={pendingQuestionId === null}
          />
        )}
      </div>
    </div>
  )
}

export default InterviewChatScreen
