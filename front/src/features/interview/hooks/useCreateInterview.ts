import { useRef } from 'react'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { createInterview, createSession, createQuestions } from '../api/interviewApi'
import type { CreateInterviewRequest } from '../api/interviewApi'
import { useInterviewStore } from '../stores/interviewStore'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/toastStore'

export const useCreateInterview = () => {
  const { setInterview, setSession, setPhase, setQuestionCount } = useInterviewStore()
  const { toast } = useToast()
  const navigate = useNavigate()

  // createQuestions 실패 시 복구용: interview+session은 생성됐으나 questions 실패
  const partialRef = useRef<{
    interviewId: number
    sessionId: number
    questionCount: number
  } | null>(null)

  const { mutate, isPending } = useMutation({
    onMutate: () => {
      setPhase('generating')
    },
    mutationFn: async (body: CreateInterviewRequest) => {
      const interview = await createInterview(body)
      const session = await createSession(interview.id)
      // interview+session 생성 후 questions 실패 시 복구 가능하도록 저장
      partialRef.current = {
        interviewId: interview.id,
        sessionId: session.sessionId,
        questionCount: interview.questionCount,
      }
      await createQuestions(interview.id)
      partialRef.current = null
      return { interviewId: interview.id, sessionId: session.sessionId, questionCount: interview.questionCount }
    },
    onSuccess: ({ interviewId, sessionId, questionCount }) => {
      setInterview(interviewId)
      setSession(sessionId)
      setQuestionCount(questionCount)
      setPhase('chat')
      navigate(`/interview/${interviewId}`)
    },
    onError: (error: unknown) => {
      const partial = partialRef.current
      partialRef.current = null

      if (partial) {
        // interview+session은 생성됐으나 createQuestions 실패
        // 면접 페이지로 이동하여 상태 가시화 — 사용자가 재시도 가능
        setInterview(partial.interviewId)
        setSession(partial.sessionId)
        setQuestionCount(partial.questionCount)
        setPhase('chat')
        navigate(`/interview/${partial.interviewId}`)
        toast('질문 생성에 실패했습니다. 잠시 후 다시 시도해주세요.', 'error')
      } else {
        // createInterview 또는 createSession 실패 — 생성된 서버 상태 없음
        setPhase('setup')
        const apiError = extractApiError(error)
        toast(getErrorMessage(apiError.code), 'error')
      }
    },
  })

  return { mutate, isPending }
}
