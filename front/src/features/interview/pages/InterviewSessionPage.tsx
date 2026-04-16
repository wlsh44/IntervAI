import { useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { Loader2 } from 'lucide-react'
import { useInterviewStore } from '../stores/interviewStore'
import InterviewChatScreen from '../components/InterviewChatScreen'
import InterviewFinishedScreen from '../components/InterviewFinishedScreen'

const InterviewSessionPage = () => {
  const { interviewId: interviewIdParam } = useParams<{ interviewId: string }>()
  const { phase, interviewId, setInterview, setPhase } = useInterviewStore()

  useEffect(() => {
    const id = Number(interviewIdParam)
    if (!interviewIdParam || isNaN(id)) return

    if (interviewId !== id) {
      // 다른 세션으로 이동 시 항상 chat으로 초기화
      setInterview(id)
      setPhase('chat')
    } else if (phase === 'setup' || phase === 'generating') {
      // 동일 세션 직접 URL 접근 시 chat 진입
      setPhase('chat')
    }
  }, [interviewIdParam, interviewId, phase, setInterview, setPhase])

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

  // setup → chat 전환 중 짧은 과도 상태
  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <Loader2 size={48} className="animate-spin text-[#4648d4]" />
      <p className="text-lg font-medium text-[#131b2e]">면접을 준비 중입니다...</p>
    </div>
  )
}

export default InterviewSessionPage
