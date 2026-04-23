import { useQuery } from '@tanstack/react-query'
import { httpClient } from '../../../shared/api/httpClient'
import { API_PATHS } from '../../../shared/api/constants'
import type { CareerLevel, JobCategory } from '../../../shared/types/enums'

interface ProfileSnapshot {
  jobCategory: JobCategory | null
  careerLevel: CareerLevel | null
  techStacks: string[]
  portfolioLinks: string[]
}

export const useLoadProfile = () => {
  return useQuery({
    queryKey: ['interview', 'profileSnapshot'],
    queryFn: () =>
      httpClient.get<ProfileSnapshot>(API_PATHS.profile.base).then((r) => r.data),
    enabled: false,
    retry: false,
  })
}
