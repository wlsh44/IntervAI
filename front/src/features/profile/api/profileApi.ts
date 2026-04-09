import { httpClient } from '../../../shared/api/httpClient'
import { API_PATHS } from '../../../shared/api/constants'
import type { JobCategory, CareerLevel } from '../../../shared/types/enums'

export interface ProfileResponse {
  id: number
  jobCategory: JobCategory | null
  careerLevel: CareerLevel | null
  techStacks: string[]
  portfolioLinks: string[]
}

export interface UpdateProfileRequest {
  jobCategory: JobCategory
  careerLevel: CareerLevel
  techStacks: string[]
  portfolioLinks: string[]
}

export const getProfile = (): Promise<ProfileResponse> =>
  httpClient.get<ProfileResponse>(API_PATHS.profile.base).then((res) => res.data)

export const createProfile = (): Promise<ProfileResponse> =>
  httpClient.post<ProfileResponse>(API_PATHS.profile.base).then((res) => res.data)

export const updateProfile = (body: UpdateProfileRequest): Promise<ProfileResponse> =>
  httpClient.put<ProfileResponse>(API_PATHS.profile.base, body).then((res) => res.data)
