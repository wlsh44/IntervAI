import { create } from 'zustand'

type InterviewPhase = 'setup' | 'generating' | 'chat' | 'finished'

interface InterviewState {
  interviewId: number | null
  sessionId: number | null
  phase: InterviewPhase
  setInterview: (interviewId: number) => void
  setSession: (sessionId: number) => void
  setPhase: (phase: InterviewPhase) => void
  resetInterview: () => void
}

export const useInterviewStore = create<InterviewState>((set) => ({
  interviewId: null,
  sessionId: null,
  phase: 'setup',
  setInterview: (interviewId) => set({ interviewId }),
  setSession: (sessionId) => set({ sessionId }),
  setPhase: (phase) => set({ phase }),
  resetInterview: () =>
    set({ interviewId: null, sessionId: null, phase: 'setup' }),
}))
