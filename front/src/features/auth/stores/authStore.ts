import { create } from 'zustand'

interface AuthState {
  accessToken: string | null
  userId: number | null
  nickname: string | null
  isInitialized: boolean
  setAuth: (accessToken: string, userId: number, nickname: string) => void
  setAccessToken: (accessToken: string) => void
  setInitialized: (isInitialized: boolean) => void
  clearAuth: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  userId: null,
  nickname: null,
  isInitialized: false,
  setAuth: (accessToken, userId, nickname) =>
    set({ accessToken, userId, nickname, isInitialized: true }),
  setAccessToken: (accessToken) => set({ accessToken }),
  setInitialized: (isInitialized) => set({ isInitialized }),
  clearAuth: () => set({ accessToken: null, userId: null, nickname: null, isInitialized: true }),
}))
