import { useMutation } from '@tanstack/react-query'
import { finishSession } from '../api/interviewApi'
import { useInterviewStore } from '../stores/interviewStore'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/toastStore'

export const useFinishSession = () => {
  const { setPhase } = useInterviewStore()
  const { toast } = useToast()

  const { mutate, isPending } = useMutation({
    mutationFn: (interviewId: number) => finishSession(interviewId),
    onSuccess: () => {
      setPhase('finished')
    },
    onError: (error: unknown) => {
      const apiError = extractApiError(error)
      if (apiError.code === 'SESSION_ALREADY_COMPLETED') {
        setPhase('finished')
        return
      }
      const message = getErrorMessage(apiError.code)
      toast(message, 'error')
    },
  })

  return { mutate, isPending }
}
