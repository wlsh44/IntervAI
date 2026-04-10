import axios from 'axios'
import { BASE_URL, API_PATHS } from './constants'

export const httpClient = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
})

// accessToken을 런타임에 주입받기 위한 setter
let accessTokenGetter: (() => string | null) | null = null
let accessTokenSetter: ((token: string) => void) | null = null
let onUnauthorized: (() => void) | null = null
let refreshPromise: Promise<string> | null = null

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

      if (!refreshPromise) {
        refreshPromise = axios
          .post(`${BASE_URL}${API_PATHS.auth.refresh}`, {}, { withCredentials: true })
          .then((res) => {
            const newToken = res.data?.accessToken
            if (typeof newToken !== 'string') {
              throw new Error('Invalid refresh response')
            }
            accessTokenSetter?.(newToken)
            return newToken
          })
          .finally(() => {
            refreshPromise = null
          })
      }

      try {
        const newToken = await refreshPromise
        originalRequest.headers.Authorization = `Bearer ${newToken}`
        return httpClient(originalRequest)
      } catch (err) {
        onUnauthorized?.()
        return Promise.reject(err)
      }
    }
    return Promise.reject(error)
  },
)
