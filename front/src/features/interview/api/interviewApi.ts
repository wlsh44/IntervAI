import { httpClient } from '../../../shared/api/httpClient'
import { API_PATHS } from '../../../shared/api/constants'
import type { CsCategory, Difficulty, InterviewType, InterviewerTone } from '../../../shared/types/enums'

export interface CsSubjectRequest {
  category: CsCategory
  topics: string[]
}

export interface CreateInterviewRequest {
  interviewType: InterviewType
  difficulty: Difficulty
  questionCount: number
  interviewerTone: InterviewerTone
  csSubjects?: CsSubjectRequest[]
  portfolioLinks?: string[]
}

export interface CreateInterviewResponse {
  id: number
  interviewType: InterviewType
  difficulty: Difficulty
  questionCount: number
  interviewerTone: InterviewerTone
  csSubjects: CsSubjectRequest[]
  portfolioLinks: string[]
}

export interface CreateSessionResponse {
  sessionId: number
}

export interface QuestionItem {
  questionId: number
  content: string
  questionIndex: number
}

export interface CreateQuestionsResponse {
  questions: QuestionItem[]
}

export const createInterview = async (body: CreateInterviewRequest): Promise<CreateInterviewResponse> => {
  const res = await httpClient.post<CreateInterviewResponse>(API_PATHS.interviews.create, body)
  return res.data
}

export const createSession = async (interviewId: number): Promise<CreateSessionResponse> => {
  const res = await httpClient.post<CreateSessionResponse>(API_PATHS.interviews.sessions(interviewId))
  return res.data
}

export const createQuestions = async (interviewId: number): Promise<CreateQuestionsResponse> => {
  const res = await httpClient.post<CreateQuestionsResponse>(API_PATHS.interviews.questions(interviewId))
  return res.data
}
