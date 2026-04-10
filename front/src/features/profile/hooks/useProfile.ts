import { useQuery } from '@tanstack/react-query'
import { queryKeys } from '../../../shared/types/queryKeys'
import { getProfile } from '../api/profileApi'
import { extractApiError } from '../../../shared/api/apiError'

export const useProfile = () => {
  return useQuery({
    queryKey: queryKeys.profile.all,
    queryFn: getProfile,
    retry: (failureCount, error) => {
      const apiError = extractApiError(error)
      if (apiError.code === 'PROFILE_NOT_FOUND') return false
      return failureCount < 1
    },
  })
}
