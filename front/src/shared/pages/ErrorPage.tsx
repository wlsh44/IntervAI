import { useNavigate } from 'react-router-dom'

interface ErrorPageProps {
  message?: string
}

const ErrorPage = ({ message = '오류가 발생했습니다.' }: ErrorPageProps) => {
  const navigate = useNavigate()

  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <p className="text-lg text-gray-700">{message}</p>
      <div className="flex gap-3">
        <button
          onClick={() => window.location.reload()}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          다시 시도
        </button>
        <button
          onClick={() => navigate('/', { replace: true })}
          className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
        >
          홈으로
        </button>
      </div>
    </div>
  )
}

export default ErrorPage
