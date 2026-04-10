export const InterviewType = {
  CS: 'CS',
  PORTFOLIO: 'PORTFOLIO',
  ALL: 'ALL',
} as const
export type InterviewType = (typeof InterviewType)[keyof typeof InterviewType]

export const Difficulty = {
  ENTRY: 'ENTRY',
  JUNIOR: 'JUNIOR',
  SENIOR: 'SENIOR',
} as const
export type Difficulty = (typeof Difficulty)[keyof typeof Difficulty]

export const InterviewerTone = {
  FRIENDLY: 'FRIENDLY',
  NORMAL: 'NORMAL',
  AGGRESSIVE: 'AGGRESSIVE',
} as const
export type InterviewerTone = (typeof InterviewerTone)[keyof typeof InterviewerTone]

export const CsCategory = {
  DATA_STRUCTURE: 'DATA_STRUCTURE',
  ALGORITHM: 'ALGORITHM',
  NETWORK: 'NETWORK',
  LANGUAGE: 'LANGUAGE',
  DATABASE: 'DATABASE',
} as const
export type CsCategory = (typeof CsCategory)[keyof typeof CsCategory]

export const QuestionType = {
  QUESTION: 'QUESTION',
  FOLLOW_UP: 'FOLLOW_UP',
} as const
export type QuestionType = (typeof QuestionType)[keyof typeof QuestionType]

export const JobCategory = {
  FRONTEND: 'FRONTEND',
  BACKEND: 'BACKEND',
  FULLSTACK: 'FULLSTACK',
  ANDROID: 'ANDROID',
  IOS: 'IOS',
  DEVOPS: 'DEVOPS',
  DATA_ENGINEER: 'DATA_ENGINEER',
  ML_ENGINEER: 'ML_ENGINEER',
} as const
export type JobCategory = (typeof JobCategory)[keyof typeof JobCategory]

export const CareerLevel = {
  ENTRY: 'ENTRY',
  JUNIOR: 'JUNIOR',
  SENIOR: 'SENIOR',
} as const
export type CareerLevel = (typeof CareerLevel)[keyof typeof CareerLevel]

export const SessionStatus = {
  IN_PROGRESS: 'IN_PROGRESS',
  COMPLETED: 'COMPLETED',
} as const
export type SessionStatus = (typeof SessionStatus)[keyof typeof SessionStatus]
