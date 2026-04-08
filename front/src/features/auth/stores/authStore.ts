import { create } from 'zustand'

interface AuthState {
  accessToken: string | null
  userId: number | null
  nickname: string | null
  setAuth: (accessToken: string, userId: number, nickname: string) => void
  setAccessToken: (accessToken: string) => void
  clearAuth: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  userId: null,
  nickname: null,
  setAuth: (accessToken, userId, nickname) =>
    set({ accessToken, userId, nickname }),
  setAccessToken: (accessToken) => set({ accessToken }),
  clearAuth: () => set({ accessToken: null, userId: null, nickname: null }),
}))
