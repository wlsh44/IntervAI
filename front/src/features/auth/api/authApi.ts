import { httpClient } from '../../../shared/api/httpClient'

interface AuthRequest {
  nickname: string
  password: string
}

interface AuthResponse {
  id: number
  nickname: string
  accessToken: string
}

export async function login(body: AuthRequest): Promise<AuthResponse> {
  const res = await httpClient.post<AuthResponse>('/api/users/login', body)
  return res.data
}

export async function signUp(body: AuthRequest): Promise<AuthResponse> {
  const res = await httpClient.post<AuthResponse>('/api/users/sign-up', body)
  return res.data
}
