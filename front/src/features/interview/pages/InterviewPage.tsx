import { useEffect } from 'react'
import { Loader2 } from 'lucide-react'
import { useInterviewStore } from '../stores/interviewStore'
import InterviewSetupForm from '../components/InterviewSetupForm'

const InterviewPage = () => {
  const phase = useInterviewStore((s) => s.phase)
  const resetInterview = useInterviewStore((s) => s.resetInterview)

  useEffect(() => {
    resetInterview()
  }, [])

  return (
    <div className="min-h-screen bg-[#faf8ff]">
      {phase === 'setup' && <InterviewSetupForm />}

      {phase === 'generating' && (
        <div className="flex flex-col items-center justify-center min-h-screen gap-4">
          <Loader2 size={48} className="animate-spin text-[#4648d4]" />
          <p className="text-lg font-medium text-[#131b2e]">면접을 준비 중입니다...</p>
          <p className="text-sm text-[#767586]">AI가 맞춤형 면접 질문을 생성하고 있습니다. 잠시만 기다려주세요.</p>
        </div>
      )}

      {phase === 'chat' && (
        <div className="p-8">
          <p className="text-gray-500">면접 진행 화면 (구현 예정)</p>
        </div>
      )}

      {phase === 'finished' && (
        <div className="p-8">
          <p className="text-gray-500">면접 결과 화면 (구현 예정)</p>
        </div>
      )}
    </div>
  )
}

export default InterviewPage
