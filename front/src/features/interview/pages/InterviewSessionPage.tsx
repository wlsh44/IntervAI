import { Loader2 } from 'lucide-react'
import { useInterviewStore } from '../stores/interviewStore'
import InterviewChatScreen from '../components/InterviewChatScreen'
import InterviewFinishedScreen from '../components/InterviewFinishedScreen'

const InterviewSessionPage = () => {
  const phase = useInterviewStore((s) => s.phase)

  if (phase === 'chat') {
    return (
      <div className="flex flex-col h-screen bg-[#faf8ff]">
        <InterviewChatScreen />
      </div>
    )
  }

  if (phase === 'finished') {
    return <InterviewFinishedScreen />
  }

  // setup → generating 진입 중 (createInterview 완료 전 짧은 과도 상태)
  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <Loader2 size={48} className="animate-spin text-[#4648d4]" />
      <p className="text-lg font-medium text-[#131b2e]">면접을 준비 중입니다...</p>
    </div>
  )
}

export default InterviewSessionPage
