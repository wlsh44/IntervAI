import { useMutation } from '@tanstack/react-query'
import { createInterview, createSession, createQuestions } from '../api/interviewApi'
import type { CreateInterviewRequest } from '../api/interviewApi'
import { useInterviewStore } from '../stores/interviewStore'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/toastStore'

export const useCreateInterview = () => {
  const { setInterview, setSession, setPhase } = useInterviewStore()
  const { toast } = useToast()

  const { mutate, isPending } = useMutation({
    onMutate: () => {
      setPhase('generating')
    },
    mutationFn: async (body: CreateInterviewRequest) => {
      const interview = await createInterview(body)
      const session = await createSession(interview.id)
      await createQuestions(interview.id)
      return { interviewId: interview.id, sessionId: session.sessionId }
    },
    onSuccess: ({ interviewId, sessionId }) => {
      setInterview(interviewId)
      setSession(sessionId)
      setPhase('chat')
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
