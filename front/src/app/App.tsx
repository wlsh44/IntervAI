import { useEffect, useRef } from 'react'
import { BrowserRouter, useNavigate, useLocation } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import axios from 'axios'
import { useAuthStore } from '../features/auth/stores/authStore'
import { setupHttpClient } from '../shared/api/httpClient'
import Router from './router'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 60_000,
    },
    mutations: {
      retry: 0,
    },
  },
})

const PUBLIC_PATHS = ['/login', '/register']

const AppInitializer = () => {
  const { accessToken, setAuth, clearAuth } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()
  const initialized = useRef(false)

  useEffect(() => {
    const { getState } = useAuthStore

    setupHttpClient(
      () => getState().accessToken,
      (token) => {
        const state = getState()
        if (state.userId !== null && state.nickname !== null) {
          getState().setAuth(token, state.userId, state.nickname)
        }
      },
      () => {
        getState().clearAuth()
        navigate('/login', { replace: true })
      },
    )
  }, [navigate])

  useEffect(() => {
    if (initialized.current) return
    initialized.current = true

    if (accessToken) return

    const isPublicPath = PUBLIC_PATHS.includes(location.pathname)

    axios
      .post<{ accessToken: string; id: number; nickname: string }>(
        `${import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'}/api/auth/refresh`,
        {},
        { withCredentials: true },
      )
      .then((res) => {
        const { accessToken: token, id, nickname } = res.data
        setAuth(token, id, nickname)
      })
      .catch(() => {
        clearAuth()
        if (!isPublicPath) {
          navigate('/login', { replace: true })
        }
      })
  }, [accessToken, location.pathname, setAuth, clearAuth, navigate])

  return null
}

const App = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AppInitializer />
        <Router />
      </BrowserRouter>
    </QueryClientProvider>
  )
}

export default App
