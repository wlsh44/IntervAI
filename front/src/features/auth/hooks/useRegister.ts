import { signUp } from '../api/authApi'
import { useAuthMutation } from './useAuthMutation'

export function useRegister() {
  return useAuthMutation(signUp)
}
