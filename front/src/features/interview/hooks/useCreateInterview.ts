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

  const { mutate, isPending } = useMutation({
    onMutate: () => {
      setPhase('generating')
    },
    mutationFn: async (body: CreateInterviewRequest) => {
      const interview = await createInterview(body)
      const session = await createSession(interview.id)
      await createQuestions(interview.id)
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
      setPhase('setup')
      const apiError = extractApiError(error)
      const message = getErrorMessage(apiError.code)
      toast(message, 'error')
    },
  })

  return { mutate, isPending }
}
