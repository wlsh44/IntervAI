import { useMutation } from '@tanstack/react-query'
import { submitAnswer } from '../api/interviewApi'
import type { SubmitAnswerResponse } from '../api/interviewApi'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/toastStore'

interface UseSubmitAnswerOptions {
  interviewId: number | null
  onSuccess: (data: SubmitAnswerResponse, content: string) => void
}

export const useSubmitAnswer = ({ interviewId, onSuccess }: UseSubmitAnswerOptions) => {
  const { toast } = useToast()

  const { mutate, isPending } = useMutation({
    mutationFn: ({ questionId, content }: { questionId: number; content: string }) => {
      if (interviewId === null) throw new Error('interviewId is null')
      return submitAnswer(interviewId, { questionId, content })
    },
    onSuccess: (data, variables) => {
      onSuccess(data, variables.content)
    },
    onError: (error: unknown) => {
      const apiError = extractApiError(error)
      const message = getErrorMessage(apiError.code)
      toast(message, 'error')
    },
  })

  return { mutate, isPending }
}
