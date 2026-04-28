import { useMutation } from '@tanstack/react-query'
import { submitAnswer, getSessionHistory } from '../api/interviewApi'
import type { SubmitAnswerResponse } from '../api/interviewApi'
import { ApiErrorCode, extractApiError, getErrorMessage } from '../../../shared/api/apiError'
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
    onError: (error: unknown, variables) => {
      const apiError = extractApiError(error)

      if (apiError.code === ApiErrorCode.ANSWER_ALREADY_EXISTS && interviewId !== null) {
        // 이전 요청이 서버에서 성공했으나 응답이 유실된 경우
        // 히스토리에서 실제 피드백을 조회하여 UI를 서버 상태와 동기화
        getSessionHistory(interviewId)
          .then((history) => {
            const answered = history.find(
              (item) => item.questionId === variables.questionId && item.answerId !== null,
            )
            if (answered?.score !== null && answered?.score !== undefined) {
              onSuccess({ feedback: answered.feedbackContent ?? '', score: answered.score }, variables.content)
              return
            }
            toast('답변 점수를 불러오지 못했습니다. 페이지를 새로고침해주세요.', 'error')
          })
          .catch(() => {
            toast('답변 상태를 불러오지 못했습니다. 페이지를 새로고침해주세요.', 'error')
          })
        return
      }

      toast(getErrorMessage(apiError.code), 'error')
    },
  })

  return { mutate, isPending }
}
