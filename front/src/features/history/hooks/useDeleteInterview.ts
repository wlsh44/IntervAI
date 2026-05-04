import { useMutation, useQueryClient } from '@tanstack/react-query'
import { queryKeys } from '../../../shared/types/queryKeys'
import { deleteInterview } from '../api/historyApi'

const useDeleteInterview = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (interviewId: number) => deleteInterview(interviewId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.interviews.all })
    },
  })
}

export default useDeleteInterview
