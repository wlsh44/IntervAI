import axios from 'axios'

export const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  withCredentials: true,
})

// accessToken을 런타임에 주입받기 위한 setter
let accessTokenGetter: (() => string | null) | null = null
let accessTokenSetter: ((token: string) => void) | null = null
let onUnauthorized: (() => void) | null = null

export function setupHttpClient(
  getToken: () => string | null,
  setToken: (token: string) => void,
  handleUnauthorized: () => void,
) {
  accessTokenGetter = getToken
  accessTokenSetter = setToken
  onUnauthorized = handleUnauthorized
}

httpClient.interceptors.request.use((config) => {
  const token = accessTokenGetter?.()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

httpClient.interceptors.response.use(
  (res) => res,
  async (error) => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      try {
        const res = await axios.post(
          `${import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'}/api/auth/refresh`,
          {},
          { withCredentials: true },
        )
        const newToken: string = res.data.accessToken
        accessTokenSetter?.(newToken)
        originalRequest.headers.Authorization = `Bearer ${newToken}`
        return httpClient(originalRequest)
      } catch {
        onUnauthorized?.()
      }
    }
    return Promise.reject(error)
  },
)
