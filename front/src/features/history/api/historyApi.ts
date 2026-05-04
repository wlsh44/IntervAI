import { httpClient } from '../../../shared/api/httpClient'
import { API_PATHS } from '../../../shared/api/constants'
import type { InterviewType, SessionStatus } from '../../../shared/types/enums'
import type { InterviewListResponse } from '../../dashboard/api/dashboardApi'

export interface HistoryListParams {
  page?: number
  size?: number
  keyword?: string
  startDate?: string
  endDate?: string
  interviewType?: InterviewType
  status?: SessionStatus
}

export const getInterviewHistory = (params?: HistoryListParams): Promise<InterviewListResponse> => {
  const cleanedParams = params
    ? Object.fromEntries(Object.entries(params).filter(([, v]) => v !== undefined && v !== ''))
    : undefined
  return httpClient
    .get<InterviewListResponse>(API_PATHS.interviews.list, { params: cleanedParams })
    .then((res) => res.data)
}

export const deleteInterview = (interviewId: number): Promise<void> => {
  return httpClient.delete(API_PATHS.interviews.delete(interviewId)).then(() => undefined)
}
