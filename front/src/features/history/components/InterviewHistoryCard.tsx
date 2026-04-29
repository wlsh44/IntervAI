import { useNavigate } from 'react-router-dom'
import type { InterviewSummary } from '../types'
import { formatHistoryDate } from '../utils/formatDate'

const INTERVIEW_TYPE_LABELS: Record<string, string> = {
  CS: 'CS INTERVIEW',
  PORTFOLIO: 'PORTFOLIO',
  ALL: 'ALL ROUNDER',
}

const INTERVIEW_TYPE_COLORS: Record<string, string> = {
  CS: 'bg-blue-100 text-blue-700',
  PORTFOLIO: 'bg-purple-100 text-purple-700',
  ALL: 'bg-green-100 text-green-700',
}

interface Props {
  interview: InterviewSummary
  onDelete: (id: number) => void
}

const InterviewHistoryCard = ({ interview, onDelete }: Props) => {
  const navigate = useNavigate()
  const isCompleted = interview.sessionStatus === 'COMPLETED'

  return (
    <div className="bg-white rounded-xl border border-[#e8e8f0] p-5 flex flex-col gap-3">
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-2 flex-wrap">
          <span
            className={`text-xs font-semibold px-2 py-0.5 rounded ${INTERVIEW_TYPE_COLORS[interview.interviewType] ?? 'bg-gray-100 text-gray-700'}`}
          >
            {INTERVIEW_TYPE_LABELS[interview.interviewType] ?? interview.interviewType}
          </span>
          <span className="text-xs font-semibold px-2 py-0.5 rounded bg-gray-100 text-gray-600">
            {interview.difficulty}
          </span>
        </div>
        <div className="flex items-center gap-2">
          <div className="text-right">
            <p className="text-[10px] font-semibold text-gray-400 uppercase tracking-wide">AI SCORE</p>
            <p className="text-lg font-bold text-gray-300">--<span className="text-xs font-normal text-gray-400">/100</span></p>
          </div>
          <button
            onClick={() => onDelete(interview.id)}
            className="ml-1 text-gray-300 hover:text-red-400 transition-colors"
            aria-label="면접 삭제"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <polyline points="3 6 5 6 21 6" />
              <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
              <path d="M10 11v6M14 11v6" />
              <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
            </svg>
          </button>
        </div>
      </div>

      <div>
        <p className="text-sm text-gray-500">
          {formatHistoryDate(interview.createdAt)} · {interview.questionCount}문항
        </p>
      </div>

      <div className="flex items-center justify-between">
        <span className={`text-sm font-medium ${isCompleted ? 'text-green-600' : 'text-orange-500'}`}>
          ● {isCompleted ? '완료' : '진행중'}
        </span>
        {isCompleted ? (
          <button
            onClick={() => navigate(`/interviews/${interview.id}/result`)}
            className="text-sm font-medium text-gray-600 border border-gray-300 rounded px-3 py-1.5 hover:bg-gray-50 transition-colors"
          >
            결과 보기 &gt;
          </button>
        ) : (
          <button
            disabled
            className="text-sm font-medium text-white bg-blue-600 rounded px-3 py-1.5 opacity-60 cursor-not-allowed"
          >
            이어하기
          </button>
        )}
      </div>
    </div>
  )
}

export default InterviewHistoryCard
