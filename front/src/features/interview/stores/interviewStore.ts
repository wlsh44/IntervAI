import { create } from 'zustand'

type InterviewPhase = 'setup' | 'generating' | 'chat' | 'finished'

interface InterviewState {
  interviewId: number | null
  sessionId: number | null
  phase: InterviewPhase
  questionCount: number | null
  currentQuestionIndex: number
  setInterview: (interviewId: number) => void
  setSession: (sessionId: number) => void
  setPhase: (phase: InterviewPhase) => void
  setQuestionCount: (count: number) => void
  setCurrentQuestionIndex: (index: number) => void
  incrementQuestionIndex: () => void
  resetInterview: () => void
}

export const useInterviewStore = create<InterviewState>((set) => ({
  interviewId: null,
  sessionId: null,
  phase: 'setup',
  questionCount: null,
  currentQuestionIndex: 0,
  setInterview: (interviewId) => set({ interviewId }),
  setSession: (sessionId) => set({ sessionId }),
  setPhase: (phase) => set({ phase }),
  setQuestionCount: (count) => set({ questionCount: count }),
  setCurrentQuestionIndex: (index) => set({ currentQuestionIndex: index }),
  incrementQuestionIndex: () => set((state) => ({ currentQuestionIndex: state.currentQuestionIndex + 1 })),
  resetInterview: () =>
    set({ interviewId: null, sessionId: null, phase: 'setup', questionCount: null, currentQuestionIndex: 0 }),
}))
