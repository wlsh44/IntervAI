import { useMutation, useQueryClient } from '@tanstack/react-query'
import { queryKeys } from '../../../shared/types/queryKeys'
import { updateProfile } from '../api/profileApi'
import type { UpdateProfileRequest } from '../api/profileApi'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/toastStore'

export const useUpdateProfile = () => {
  const queryClient = useQueryClient()
  const { toast } = useToast()

  return useMutation({
    mutationFn: (body: UpdateProfileRequest) => updateProfile(body),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.profile.all })
      toast('프로필이 저장되었습니다.', 'success')
    },
    onError: (error) => {
      const apiError = extractApiError(error)
      toast(getErrorMessage(apiError.code), 'error')
    },
  })
}
