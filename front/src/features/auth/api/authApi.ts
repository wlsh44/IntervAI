import axios from 'axios'
import { BASE_URL, API_PATHS } from '../../../shared/api/constants'

// 인증 API는 refresh 인터셉터를 우회하기 위해 별도 axios 인스턴스 사용
const authClient = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
})

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
  const res = await authClient.post<AuthResponse>(API_PATHS.users.login, body)
  return res.data
}

export async function signUp(body: AuthRequest): Promise<AuthResponse> {
  const res = await authClient.post<AuthResponse>(API_PATHS.users.signUp, body)
  return res.data
}
