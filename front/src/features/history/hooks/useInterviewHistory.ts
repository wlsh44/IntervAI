import { useQuery } from '@tanstack/react-query'
import { queryKeys } from '@/shared/types/queryKeys'
import { getInterviewList } from '../api/historyApi'
import type { InterviewListParams } from '../types'

const useInterviewHistory = (params: InterviewListParams) => {
  return useQuery({
    queryKey: queryKeys.interviews.list(params as Record<string, unknown>),
    queryFn: () => getInterviewList(params),
    placeholderData: (prev) => prev,
  })
}

export default useInterviewHistory
