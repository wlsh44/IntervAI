import { useMutation } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { signUp } from '../api/authApi'
import { useAuthStore } from '../stores/authStore'
import { extractApiError, getErrorMessage } from '../../../shared/api/apiError'
import { useToast } from '../../../shared/components/ui/Toast'

export function useRegister() {
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const { toast } = useToast()

  return useMutation({
    mutationFn: signUp,
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
