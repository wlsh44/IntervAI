import { httpClient } from '../../../shared/api/httpClient'
import { API_PATHS } from '../../../shared/api/constants'
import type { InterviewType, Difficulty, SessionStatus } from '../../../shared/types/enums'

export interface InterviewSummary {
  id: number
  interviewType: InterviewType
  difficulty: Difficulty
  questionCount: number
  sessionStatus: SessionStatus
  createdAt: string
}

export interface InterviewListResponse {
  content: InterviewSummary[]
  totalElements: number
  totalPages: number
  last: boolean
}

export const getInterviewList = (params?: {
  page?: number
  size?: number
}): Promise<InterviewListResponse> => {
  return httpClient.get<InterviewListResponse>(API_PATHS.interviews.list, { params }).then((res) => res.data)
}
