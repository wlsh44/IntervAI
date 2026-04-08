import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../../features/auth/stores/authStore'

const navItems = [
  { label: '대시보드', to: '/' },
  { label: '새 면접', to: '/interview' },
  { label: '히스토리', to: '/history' },
]

const AppLayout = () => {
  const nickname = useAuthStore((s) => s.nickname)
  const clearAuth = useAuthStore((s) => s.clearAuth)
  const navigate = useNavigate()

  const handleLogout = () => {
    clearAuth()
    navigate('/login', { replace: true })
  }

  return (
    <div className="flex min-h-screen">
      <aside className="w-60 flex-shrink-0 bg-gray-900 text-white flex flex-col">
        <div className="px-6 py-5 text-xl font-bold tracking-tight">
          IntervAI
        </div>

        <nav className="flex-1 px-3 space-y-1">
          {navItems.map(({ label, to }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              className={({ isActive }) =>
                `block px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-gray-700 text-white'
                    : 'text-gray-400 hover:bg-gray-800 hover:text-white'
                }`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="px-4 py-4 border-t border-gray-700 flex items-center justify-between gap-2">
          <span className="text-sm text-gray-300 truncate">{nickname ?? ''}</span>
          <button
            onClick={handleLogout}
            className="text-xs text-gray-400 hover:text-white px-2 py-1 rounded hover:bg-gray-800 transition-colors flex-shrink-0"
          >
            로그아웃
          </button>
        </div>
      </aside>

      <main className="flex-1 bg-gray-50 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}

export default AppLayout
