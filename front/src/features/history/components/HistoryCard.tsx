import { Link } from 'react-router-dom'
import type { InterviewSummary } from '../../dashboard/api/dashboardApi'
import { InterviewType, SessionStatus, Difficulty } from '../../../shared/types/enums'
import { formatDate } from '../../../shared/utils/date'

interface HistoryCardProps {
  item: InterviewSummary
  onDelete: (id: number) => void
  isDeleting: boolean
}

const INTERVIEW_TYPE_LABEL: Record<InterviewType, string> = {
  [InterviewType.CS]: 'CS 기초',
  [InterviewType.PORTFOLIO]: '포트폴리오',
  [InterviewType.ALL]: '종합',
}

const DIFFICULTY_LABEL: Record<Difficulty, string> = {
  [Difficulty.ENTRY]: '입문',
  [Difficulty.JUNIOR]: '주니어',
  [Difficulty.SENIOR]: '시니어',
}

const SESSION_STATUS_LABEL: Record<SessionStatus, string> = {
  [SessionStatus.IN_PROGRESS]: '진행 중',
  [SessionStatus.COMPLETED]: '완료',
}

const SESSION_STATUS_COLOR: Record<SessionStatus, string> = {
  [SessionStatus.IN_PROGRESS]: 'text-amber-600 bg-amber-50',
  [SessionStatus.COMPLETED]: 'text-green-600 bg-green-50',
}

const HistoryCard = ({ item, onDelete, isDeleting }: HistoryCardProps) => {
  const actionTo =
    item.sessionStatus === SessionStatus.IN_PROGRESS
      ? `/interview/${item.id}`
      : `/interviews/${item.id}/result`
  const actionLabel = item.sessionStatus === SessionStatus.IN_PROGRESS ? '이어하기' : '결과 보기'

  const handleDelete = () => {
    if (window.confirm('이 면접 기록을 삭제하시겠습니까?')) {
      onDelete(item.id)
    }
  }

  return (
    <li className="flex items-center justify-between p-4 bg-white rounded-xl border border-[#e2e7ff]">
      <div className="flex items-center gap-4 min-w-0">
        <div className="flex flex-col gap-1 min-w-0">
          <div className="flex items-center gap-2">
            <span className="text-xs font-semibold text-[#4648d4] bg-[#eaedff] px-2 py-0.5 rounded-full">
              {INTERVIEW_TYPE_LABEL[item.interviewType]}
            </span>
            <span className="text-xs text-[#131b2e]/50">{DIFFICULTY_LABEL[item.difficulty]}</span>
          </div>
          <p className="text-sm text-[#131b2e]/60 mt-0.5">
            {formatDate(item.createdAt)} · {item.questionCount}문항
          </p>
        </div>
      </div>

      <div className="flex items-center gap-4 shrink-0">
        <div className="text-right">
          <p className="text-xs text-[#131b2e]/40">AI SCORE</p>
          <p className="text-base font-bold text-[#131b2e]">
            {item.totalScore != null ? `${item.totalScore}/100` : '--'}
          </p>
        </div>

        <span
          className={`text-xs font-medium px-2 py-1 rounded-full ${SESSION_STATUS_COLOR[item.sessionStatus]}`}
        >
          {SESSION_STATUS_LABEL[item.sessionStatus]}
        </span>

        <Link
          to={actionTo}
          className="text-sm font-medium text-[#4648d4] border border-[#4648d4] px-3 py-1.5 rounded-lg hover:bg-[#eaedff] transition-colors"
        >
          {actionLabel}
        </Link>

        <button
          onClick={handleDelete}
          disabled={isDeleting}
          className="text-sm text-[#131b2e]/40 hover:text-red-500 transition-colors disabled:opacity-50"
        >
          삭제
        </button>
      </div>
    </li>
  )
}

export default HistoryCard
