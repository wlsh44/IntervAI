import { useQuery } from '@tanstack/react-query'
import { queryKeys } from '../../../shared/types/queryKeys'
import { getInterviewList } from '../api/dashboardApi'

const useInterviewList = () => {
  return useQuery({
    queryKey: queryKeys.interviews.list({ size: 3 }),
    queryFn: () => getInterviewList({ size: 3 }),
    staleTime: 60_000,
    retry: false,
  })
}

export default useInterviewList
