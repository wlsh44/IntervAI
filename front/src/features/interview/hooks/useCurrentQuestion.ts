import { useQuery } from '@tanstack/react-query'
import { getCurrentQuestion } from '../api/interviewApi'
import { queryKeys } from '../../../shared/types/queryKeys'

export const useCurrentQuestion = (interviewId: number | null, enabled: boolean) => {
  return useQuery({
    queryKey: queryKeys.interview.currentQuestion(interviewId ?? 0),
    queryFn: () => getCurrentQuestion(interviewId!),
    enabled: enabled && interviewId !== null,
    staleTime: Infinity,
    retry: false,
  })
}
