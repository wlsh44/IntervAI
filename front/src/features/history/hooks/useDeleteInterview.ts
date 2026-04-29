import { useMutation, useQueryClient } from '@tanstack/react-query'
import { queryKeys } from '@/shared/types/queryKeys'
import { deleteInterview } from '../api/historyApi'

const useDeleteInterview = (onSuccess?: () => void) => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (interviewId: number) => deleteInterview(interviewId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.interviews.all })
      onSuccess?.()
    },
  })
}

export default useDeleteInterview
