import { Routes, Route } from 'react-router-dom'
import PrivateRoute from '../shared/components/layout/PrivateRoute'
import AppLayout from '../shared/components/layout/AppLayout'
import LoginPage from '../features/auth/pages/LoginPage'
import RegisterPage from '../features/auth/pages/RegisterPage'
import DashboardPage from '../shared/pages/DashboardPage'
import InterviewPage from '../features/interview/pages/InterviewPage'
import ProfilePage from '../features/profile/pages/ProfilePage'
import NotFoundPage from '../shared/pages/NotFoundPage'

const Router = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<PrivateRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/interview" element={<InterviewPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}

export default Router
