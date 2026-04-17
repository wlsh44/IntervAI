import { useQuery } from '@tanstack/react-query'
import { getInterviewReport } from '../api/interviewApi'
import { queryKeys } from '../../../shared/types/queryKeys'

export const useInterviewReport = (id: number) => {
  return useQuery({
    queryKey: queryKeys.interview.report(id),
    queryFn: () => getInterviewReport(id),
    enabled: !isNaN(id),
    retry: false,
  })
}
