import { httpClient } from '@/shared/api/httpClient'
import { API_PATHS } from '@/shared/api/constants'
import type { InterviewListParams, InterviewListResponse } from '../types'

export const getInterviewList = (params: InterviewListParams): Promise<InterviewListResponse> =>
  httpClient.get<InterviewListResponse>(API_PATHS.interviews.list, { params }).then((res) => res.data)

export const deleteInterview = (interviewId: number): Promise<void> =>
  httpClient.delete(API_PATHS.interviews.delete(interviewId)).then(() => undefined)
