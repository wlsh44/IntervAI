export const queryKeys = {
  profile: {
    all: ['profile'] as const,
    detail: (profileId: number) => ['profile', profileId] as const,
  },
  interview: {
    all: ['interview'] as const,
    currentQuestion: (interviewId: number) =>
      ['interview', interviewId, 'currentQuestion'] as const,
    report: (interviewId: number) => ['interview', interviewId, 'report'] as const,
    history: (interviewId: number) => ['interview', interviewId, 'history'] as const,
  },
  interviews: {
    all: ['interviews'] as const,
    list: () => ['interviews', 'list'] as const,
  },
} as const
