import { useMutation, useQueryClient } from '@tanstack/react-query'
import { queryKeys } from '../../../shared/types/queryKeys'
import { createProfile } from '../api/profileApi'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/toastStore'

export const useCreateProfile = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: createProfile,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.profile.all })
    },
    onError: (error) => {
      const apiError = extractApiError(error)
      toast(getErrorMessage(apiError.code), 'error')
    },
  })
}
