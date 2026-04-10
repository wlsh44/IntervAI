import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuthStore } from '../../../features/auth/stores/authStore'

const PrivateRoute = () => {
  const accessToken = useAuthStore((s) => s.accessToken)
  const location = useLocation()

  if (!accessToken) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />
  }

  return <Outlet />
}

export default PrivateRoute
