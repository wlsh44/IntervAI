import { useNavigate } from 'react-router-dom'
import { useInterviewStore } from '../stores/interviewStore'

const InterviewFinishedScreen = () => {
  const navigate = useNavigate()
  const { interviewId, resetInterview } = useInterviewStore()

  const handleGoToResult = () => {
    if (interviewId === null) return
    navigate(`/interviews/${interviewId}/result`)
  }

  const handleNewInterview = () => {
    resetInterview()
    navigate('/interview')
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-6 bg-[#faf8ff] px-4">
      <div className="flex flex-col items-center gap-3 text-center">
        <div className="w-16 h-16 rounded-full bg-[#eaedff] flex items-center justify-center">
          <span className="text-3xl">🎉</span>
        </div>
        <h1 className="text-2xl font-bold text-[#131b2e]">면접이 완료되었습니다!</h1>
        <p className="text-sm text-[#767586] max-w-xs">
          수고하셨습니다. 면접 결과를 확인하거나 새 면접을 시작할 수 있습니다.
        </p>
      </div>

      <div className="flex flex-col gap-3 w-full max-w-xs">
        <button
          onClick={handleGoToResult}
          className="bg-[#4648d4] text-white text-sm font-medium px-8 py-3 rounded-lg hover:bg-[#3a3bb8] transition-colors"
        >
          면접 결과 보러가기
        </button>
        <button
          onClick={handleNewInterview}
          className="border border-[#4648d4] text-[#4648d4] text-sm font-medium px-8 py-3 rounded-lg hover:bg-[#eaedff] transition-colors"
        >
          새 면접 시작
        </button>
      </div>
    </div>
  )
}

export default InterviewFinishedScreen
