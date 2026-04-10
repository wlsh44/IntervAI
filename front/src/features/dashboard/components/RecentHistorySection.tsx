import { Link } from 'react-router-dom'
import type { InterviewSummary } from '../api/dashboardApi'
import { InterviewType, SessionStatus } from '../../../shared/types/enums'

interface RecentHistorySectionProps {
  items: InterviewSummary[]
}

const INTERVIEW_TYPE_LABEL: Record<string, string> = {
  [InterviewType.CS]: 'CS 기초',
  [InterviewType.PORTFOLIO]: '포트폴리오',
  [InterviewType.ALL]: '종합',
}

const SESSION_STATUS_LABEL: Record<string, string> = {
  [SessionStatus.IN_PROGRESS]: '진행 중',
  [SessionStatus.COMPLETED]: '완료',
}

const SESSION_STATUS_COLOR: Record<string, string> = {
  [SessionStatus.IN_PROGRESS]: 'text-amber-600 bg-amber-50',
  [SessionStatus.COMPLETED]: 'text-green-600 bg-green-50',
}

const formatDate = (createdAt: string): string => {
  return new Date(createdAt).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

const RecentHistorySection = ({ items }: RecentHistorySectionProps) => {
  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-[#131b2e] font-bold text-lg">최근 면접 기록</h2>
        <Link to="/history" className="text-sm text-[#4648d4] hover:underline">
          더보기
        </Link>
      </div>

      {items.length === 0 ? (
        <p className="text-[#131b2e]/40 text-center py-8">아직 면접 기록이 없습니다</p>
      ) : (
        <ul className="space-y-3">
          {items.map((item) => (
            <li
              key={item.id}
              className="flex items-center justify-between p-4 bg-white rounded-xl border border-[#e2e7ff] hover:bg-[#faf8ff]"
            >
              <div>
                <p className="text-[#131b2e] font-medium">
                  {INTERVIEW_TYPE_LABEL[item.interviewType] ?? item.interviewType}
                </p>
                <p className="text-sm text-[#131b2e]/50 mt-0.5">{formatDate(item.createdAt)}</p>
              </div>
              <div className="flex items-center gap-3">
                <span
                  className={`text-xs font-medium px-2 py-1 rounded-full ${SESSION_STATUS_COLOR[item.sessionStatus] ?? ''}`}
                >
                  {SESSION_STATUS_LABEL[item.sessionStatus] ?? item.sessionStatus}
                </span>
                <span className="text-[#4648d4] text-lg">›</span>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default RecentHistorySection
