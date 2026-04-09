import { login } from '../api/authApi'
import { useAuthMutation } from './useAuthMutation'

export function useLogin() {
  return useAuthMutation(login)
}
