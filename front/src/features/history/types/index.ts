import type { InterviewType, Difficulty, SessionStatus } from '@/shared/types/enums'

export type { InterviewType, Difficulty, SessionStatus }

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

export interface InterviewListParams {
  page?: number
  size?: number
  interviewType?: InterviewType
  sessionStatus?: SessionStatus
}

export interface HistoryFilterState {
  keyword: string
  interviewType: InterviewType | ''
  sessionStatus: SessionStatus | ''
}
