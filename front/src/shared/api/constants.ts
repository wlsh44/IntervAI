export const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export const API_PATHS = {
  auth: {
    refresh: '/api/auth/refresh',
  },
  users: {
    login: '/api/users/login',
    signUp: '/api/users/sign-up',
  },
  profile: {
    base: '/api/users/profile',
  },
  interviews: {
    list: '/api/interviews',
    create: '/api/interviews',
    finish: (interviewId: number) => `/api/interviews/${interviewId}/finish`,
    sessions: (interviewId: number) => `/api/interviews/${interviewId}/sessions`,
    finishSession: (interviewId: number) => `/api/interviews/${interviewId}/sessions/finish`,
    history: (interviewId: number) => `/api/interviews/${interviewId}/sessions/history`,
    questions: (interviewId: number) => `/api/interviews/${interviewId}/questions`,
    currentQuestion: (interviewId: number) => `/api/interviews/${interviewId}/questions/current`,
    answers: (interviewId: number) => `/api/interviews/${interviewId}/answers`,
  },
} as const
