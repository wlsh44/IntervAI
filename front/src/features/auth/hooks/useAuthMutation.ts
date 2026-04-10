import { useMutation } from '@tanstack/react-query'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../stores/authStore'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/toastStore'
import type { login } from '../api/authApi'

type AuthMutationFn = typeof login

export function useAuthMutation(mutationFn: AuthMutationFn) {
  const navigate = useNavigate()
  const location = useLocation()
  const setAuth = useAuthStore((s) => s.setAuth)
  const { toast } = useToast()

  return useMutation({
    mutationFn,
    onSuccess: (data) => {
      setAuth(data.accessToken, data.id, data.nickname)
      const from = (location.state as { from?: string } | null)?.from ?? '/'
      navigate(from, { replace: true })
    },
    onError: (error) => {
      const { code } = extractApiError(error)
      toast(getErrorMessage(code), 'error')
    },
  })
}
