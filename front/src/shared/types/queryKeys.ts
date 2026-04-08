export const queryKeys = {
  profile: {
    all: ['profile'] as const,
    detail: (profileId: number) => ['profile', profileId] as const,
  },
  interview: {
    all: ['interview'] as const,
    currentQuestion: (interviewId: number) =>
      ['interview', interviewId, 'currentQuestion'] as const,
  },
} as const
