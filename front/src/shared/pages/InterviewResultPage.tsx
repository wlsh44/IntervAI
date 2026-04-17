import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Loader2, ChevronDown, ChevronUp } from 'lucide-react'
import { useState } from 'react'
import { getSessionHistory } from '../../features/interview/api/interviewApi'
import { QuestionType } from '../types/enums'
import type { OrderedSessionHistoryItem } from '../../features/interview/utils/sessionHistory'
import { orderSessionHistory } from '../../features/interview/utils/sessionHistory'

const ResultItem = ({ item }: { item: OrderedSessionHistoryItem }) => {
  const [feedbackOpen, setFeedbackOpen] = useState(false)
  const isFollowUp = item.questionType === QuestionType.FOLLOW_UP
  const depthClass = item.depth > 0 ? 'ml-6 border-l-4 border-[#d7dcff]' : ''
  const displayOrder = Math.max(1, item.mainQuestionOrder)
  const label = isFollowUp ? `Q${displayOrder} 꼬리 질문` : `Q${displayOrder}`

  return (
    <div className={`bg-white rounded-xl border border-[#c7cbf5] overflow-hidden ${depthClass}`}>
      <div className="px-5 py-4 border-b border-[#eaedff]">
        <div className="flex items-center gap-2 mb-2">
          <span className="text-xs font-medium text-[#4648d4] bg-[#eaedff] px-2 py-0.5 rounded-full">
            {label}
          </span>
          {isFollowUp && item.parentQuestionId !== null && (
            <span className="text-[11px] font-medium text-[#767586]">
              이전 질문에 대한 추가 질문
            </span>
          )}
        </div>
        <p className="text-sm font-medium text-[#131b2e]">{item.questionContent}</p>
      </div>

      {item.answerContent ? (
        <div className="px-5 py-4">
          <p className="text-xs text-[#767586] mb-1">내 답변</p>
          <p className="text-sm text-[#131b2e] whitespace-pre-wrap">{item.answerContent}</p>

          {item.feedbackContent && (
            <div className="mt-3">
              <button
                onClick={() => setFeedbackOpen((prev) => !prev)}
                className="flex items-center gap-1 text-xs font-medium text-[#4648d4] hover:underline"
              >
                AI 피드백
                {feedbackOpen ? <ChevronUp size={12} /> : <ChevronDown size={12} />}
              </button>
              {feedbackOpen && (
                <p className="mt-2 text-sm text-[#444] bg-[#faf8ff] border border-[#eaedff] rounded-lg px-4 py-3 whitespace-pre-wrap">
                  {item.feedbackContent}
                </p>
              )}
            </div>
          )}
        </div>
      ) : (
        <div className="px-5 py-4">
          <p className="text-sm text-[#767586] italic">답변 없음</p>
        </div>
      )}
    </div>
  )
}

const InterviewResultPage = () => {
  const { interviewId } = useParams<{ interviewId: string }>()
  const navigate = useNavigate()
  const id = Number(interviewId)

  const { data: history, isLoading, isError } = useQuery({
    queryKey: ['interviews', id, 'history'],
    queryFn: () => getSessionHistory(id),
    enabled: !isNaN(id),
  })

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4 bg-[#faf8ff]">
        <Loader2 size={36} className="animate-spin text-[#4648d4]" />
        <p className="text-sm text-[#767586]">결과를 불러오는 중...</p>
      </div>
    )
  }

  if (isError || !history) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4 bg-[#faf8ff]">
        <p className="text-sm text-[#767586]">결과를 불러오지 못했습니다.</p>
        <button
          onClick={() => navigate('/')}
          className="text-sm text-[#4648d4] border border-[#4648d4] rounded px-4 py-2 hover:bg-[#eaedff] transition-colors"
        >
          대시보드로 돌아가기
        </button>
      </div>
    )
  }

  const mainQuestions = history.filter((item) => item.questionType === QuestionType.QUESTION)
  const answeredCount = history.filter((item) => item.answerId !== null).length
  const orderedHistory = orderSessionHistory(history)

  return (
    <div className="min-h-screen bg-[#faf8ff]">
      <div className="max-w-2xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-xl font-bold text-[#131b2e]">면접 결과</h1>
          <button
            onClick={() => navigate('/')}
            className="text-sm text-[#767586] hover:text-[#131b2e] transition-colors"
          >
            대시보드로
          </button>
        </div>

        <div className="bg-[#eaedff] rounded-xl px-5 py-4 mb-6 flex gap-6">
          <div>
            <p className="text-xs text-[#767586]">전체 질문</p>
            <p className="text-lg font-bold text-[#131b2e]">{history.length}개</p>
          </div>
          <div>
            <p className="text-xs text-[#767586]">답변 완료</p>
            <p className="text-lg font-bold text-[#4648d4]">{answeredCount}개</p>
          </div>
          <div>
            <p className="text-xs text-[#767586]">본 질문</p>
            <p className="text-lg font-bold text-[#131b2e]">{mainQuestions.length}개</p>
          </div>
        </div>

        <div className="flex flex-col gap-4">
          {orderedHistory.map((item) => (
            <ResultItem key={item.questionId} item={item} />
          ))}
        </div>
      </div>
    </div>
  )
}

export default InterviewResultPage
