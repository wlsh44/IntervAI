import { useQuery } from '@tanstack/react-query'
import { queryKeys } from '../../../shared/types/queryKeys'
import { getInterviewHistory } from '../api/historyApi'
import type { HistoryListParams } from '../api/historyApi'

const useInterviewHistory = (filters: Omit<HistoryListParams, 'page'>, page: number) => {
  const params: HistoryListParams = { ...filters, page, size: 5 }
  return useQuery({
    queryKey: queryKeys.interviews.list(params as Record<string, unknown>),
    queryFn: () => getInterviewHistory(params),
    staleTime: 60_000,
    retry: false,
  })
}

export default useInterviewHistory
