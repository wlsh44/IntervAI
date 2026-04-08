export const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export const API_PATHS = {
  auth: {
    refresh: '/api/auth/refresh',
  },
} as const
