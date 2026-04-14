import { useEffect } from 'react'
import { Loader2 } from 'lucide-react'
import { useInterviewStore } from '../stores/interviewStore'
import InterviewSetupForm from '../components/InterviewSetupForm'

const InterviewPage = () => {
  const { phase, resetInterview } = useInterviewStore()

  // 새 면접 페이지 진입 시 항상 setup 상태로 초기화
  useEffect(() => {
    resetInterview()
  }, [resetInterview])

  if (phase === 'generating') {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4 bg-[#faf8ff]">
        <Loader2 size={48} className="animate-spin text-[#4648d4]" />
        <p className="text-lg font-medium text-[#131b2e]">면접을 준비 중입니다...</p>
        <p className="text-sm text-[#767586]">AI가 맞춤형 면접 질문을 생성하고 있습니다. 잠시만 기다려주세요.</p>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-[#faf8ff]">
      <InterviewSetupForm />
    </div>
  )
}

export default InterviewPage
