import { useNavigate } from 'react-router-dom'

const NotFoundPage = () => {
  const navigate = useNavigate()

  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <h1 className="text-4xl font-bold text-gray-800">404</h1>
      <p className="text-lg text-gray-600">페이지를 찾을 수 없습니다.</p>
      <button
        onClick={() => navigate('/', { replace: true })}
        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
      >
        대시보드로 돌아가기
      </button>
    </div>
  )
}

export default NotFoundPage
