import { Link } from 'react-router-dom'
import { useAuthStore } from '../../auth/stores/authStore'

const WelcomeSection = () => {
  const nickname = useAuthStore((s) => s.nickname)

  return (
    <div className="bg-[#eaedff] rounded-xl p-6">
      <h1 className="text-[#131b2e] text-xl font-semibold">{nickname}님, 반갑습니다!</h1>
      <p className="text-sm text-[#131b2e]/70 mt-1.5">꾸준한 연습이 합격을 만듭니다. 오늘도 함께해요.</p>
      <Link
        to="/interview"
        style={{ background: 'linear-gradient(135deg, #4648d4, #6b38d4)' }}
        className="inline-block text-sm text-white rounded-lg px-5 py-2.5 font-semibold hover:opacity-90 transition-opacity mt-5"
      >
        면접 시작하기
      </Link>
    </div>
  )
}

export default WelcomeSection
