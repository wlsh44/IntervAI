import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Loader2, ChevronDown, ChevronUp } from 'lucide-react'
import { useState } from 'react'
import { getSessionHistory } from '../../features/interview/api/interviewApi'
import { useInterviewReport } from '../../features/interview/hooks/useInterviewReport'
import { QuestionType } from '../types/enums'
import { queryKeys } from '../types/queryKeys'
import type { OrderedSessionHistoryItem } from '../../features/interview/utils/sessionHistory'
import { orderSessionHistory } from '../../features/interview/utils/sessionHistory'
import { ApiErrorCode, extractApiError } from '../api/apiError'
import ScoreBadge from '../../features/interview/components/report/ScoreBadge'
import OverallCommentCard from '../../features/interview/components/report/OverallCommentCard'
import ReportQuestionListItem from '../../features/interview/components/report/ReportQuestionListItem'

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

  const { data: history, isLoading: historyLoading, isError: historyError } = useQuery({
    queryKey: queryKeys.interview.history(id),
    queryFn: () => getSessionHistory(id),
    enabled: !isNaN(id),
  })

  const report = useInterviewReport(id)

  // 에러 분류를 로딩 판단보다 먼저 수행
  const reportErrorCode = report.isError ? extractApiError(report.error).code : null
  const isSessionNotCompleted = reportErrorCode === ApiErrorCode.SESSION_NOT_COMPLETED
  const isReportNotFound = reportErrorCode === ApiErrorCode.REPORT_NOT_FOUND

  // 리포트가 pending이면 무조건 대기
  // 리포트가 REPORT_NOT_FOUND(fallback 케이스)이면 히스토리 로딩도 완료될 때까지 대기
  // SESSION_NOT_COMPLETED나 기타 에러는 히스토리 대기 없이 즉시 렌더링
  const isLoading =
    report.isLoading || (isReportNotFound && historyLoading)

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4 bg-[#faf8ff]">
        <Loader2 size={36} className="animate-spin text-[#4648d4]" />
        <p className="text-sm text-[#767586]">결과를 불러오는 중...</p>
      </div>
    )
  }

  // 리포트 정상 수신 시 리포트 뷰
  if (report.data) {
    const { totalScore, overallComment, questions } = report.data
    return (
      <div className="min-h-screen bg-[#faf8ff]">
        <div className="max-w-2xl mx-auto px-4 py-8 flex flex-col gap-6">
          <div className="flex items-center justify-between">
            <h1 className="text-xl font-bold text-[#131b2e]">면접 결과</h1>
            <button
              onClick={() => navigate('/')}
              className="text-sm bg-[#4648d4] text-white rounded-lg px-4 py-2 hover:bg-[#3537b0] transition-colors"
            >
              대시보드로
            </button>
          </div>

          <ScoreBadge score={totalScore} />
          <OverallCommentCard comment={overallComment} />

          <div className="flex flex-col gap-4">
            <p className="text-base font-semibold text-[#131b2e]">질문별 상세</p>
            {questions.map((item, index) => (
              <ReportQuestionListItem key={item.questionId} item={item} index={index} />
            ))}
          </div>
        </div>
      </div>
    )
  }

  // 세션 미완료: 결과 페이지 접근 불가 안내
  if (isSessionNotCompleted) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4 bg-[#faf8ff]">
        <p className="text-base font-semibold text-[#131b2e]">면접이 아직 완료되지 않았습니다.</p>
        <p className="text-sm text-[#767586]">면접을 끝까지 진행한 후 결과를 확인하세요.</p>
        <button
          onClick={() => navigate(`/interview/${id}`)}
          className="text-sm bg-[#4648d4] text-white rounded-lg px-4 py-2 hover:bg-[#3537b0] transition-colors"
        >
          면접으로 돌아가기
        </button>
      </div>
    )
  }

  // 기타 리포트 에러 (REPORT_NOT_FOUND 제외)
  if (report.isError && !isReportNotFound) {
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

  // Fallback: REPORT_NOT_FOUND 시 히스토리 기반 뷰 (히스토리 로딩 완료 후 도달)
  if (historyError || !history) {
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
