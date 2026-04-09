import { useMutation } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../stores/authStore'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/Toast'
import type { login } from '../api/authApi'

type AuthMutationFn = typeof login

export function useAuthMutation(mutationFn: AuthMutationFn) {
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const { toast } = useToast()

  return useMutation({
    mutationFn,
    onSuccess: (data) => {
      setAuth(data.accessToken, data.id, data.nickname)
      navigate('/')
    },
    onError: (error) => {
      const { code } = extractApiError(error)
      toast(getErrorMessage(code), 'error')
    },
  })
}
