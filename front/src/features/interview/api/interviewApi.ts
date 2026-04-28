import { httpClient } from '../../../shared/api/httpClient'
import { API_PATHS } from '../../../shared/api/constants'
import type { CsCategory, Difficulty, InterviewType, InterviewerTone, JobCategory, QuestionType } from '../../../shared/types/enums'

export interface CsSubjectRequest {
  category: CsCategory
  topics: string[]
}

export interface CreateInterviewRequest {
  jobCategory: JobCategory
  interviewType: InterviewType
  difficulty: Difficulty
  questionCount: number
  interviewerTone: InterviewerTone
  csSubjects?: CsSubjectRequest[]
  portfolioLinks?: string[]
  techStacks?: string[]
}

export interface CreateInterviewResponse {
  id: number
  jobCategory: JobCategory
  interviewType: InterviewType
  difficulty: Difficulty
  questionCount: number
  interviewerTone: InterviewerTone
  csSubjects: CsSubjectRequest[]
  portfolioLinks: string[]
  techStacks: string[]
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

export interface CurrentQuestionResponse {
  questionId: number
  question: string
  questionType: QuestionType
  hasNext: boolean
}

export interface SubmitAnswerRequest {
  questionId: number
  content: string
}

export interface SubmitAnswerResponse {
  feedback: string
  score: number
}

export interface SessionHistoryItem {
  questionId: number
  parentQuestionId: number | null
  answerId: number | null
  questionContent: string
  answerContent: string | null
  feedbackContent: string | null
  score: number | null
  questionType: QuestionType
  questionIndex: number
}

export const createInterview = async (body: CreateInterviewRequest): Promise<CreateInterviewResponse> => {
  const res = await httpClient.post<CreateInterviewResponse>(API_PATHS.interviews.create, body)
  return res.data
}

export const createSession = async (interviewId: number): Promise<CreateSessionResponse> => {
  const res = await httpClient.post<CreateSessionResponse>(API_PATHS.interviews.sessions(interviewId))
  return res.data
}

export const finishSession = async (interviewId: number): Promise<void> => {
  await httpClient.post(API_PATHS.interviews.finishSession(interviewId))
}

export const createQuestions = async (interviewId: number): Promise<CreateQuestionsResponse> => {
  const res = await httpClient.post<CreateQuestionsResponse>(API_PATHS.interviews.questions(interviewId))
  return res.data
}

export const getCurrentQuestion = async (interviewId: number): Promise<CurrentQuestionResponse> => {
  const res = await httpClient.get<CurrentQuestionResponse>(API_PATHS.interviews.currentQuestion(interviewId))
  return res.data
}

export const submitAnswer = async (
  interviewId: number,
  body: SubmitAnswerRequest,
): Promise<SubmitAnswerResponse> => {
  const res = await httpClient.post<SubmitAnswerResponse>(API_PATHS.interviews.answers(interviewId), body)
  return res.data
}

export const getSessionHistory = async (interviewId: number): Promise<SessionHistoryItem[]> => {
  const res = await httpClient.get<SessionHistoryItem[]>(API_PATHS.interviews.history(interviewId))
  return res.data
}

export interface FollowUpQuestionItem {
  questionId: number
  questionContent: string
  answerContent: string | null
  feedbackContent: string | null
}

export interface ReportQuestionItem {
  questionId: number
  questionIndex: number
  questionContent: string
  answerContent: string | null
  feedbackContent: string | null
  score: number | null
  keywords: string[]
  followUps: FollowUpQuestionItem[]
}

export interface InterviewReport {
  interviewId: number
  interviewType: string
  jobCategory: string
  difficulty: string
  questionCount: number
  completedAt: string
  totalScore: number
  overallComment: string
  questions: ReportQuestionItem[]
}

export const getInterviewReport = async (interviewId: number): Promise<InterviewReport> => {
  const res = await httpClient.get<InterviewReport>(API_PATHS.interviews.report(interviewId))
  return res.data
}
