import type { HistoryFilterState, InterviewType, SessionStatus } from '../types'

interface Props {
  filters: HistoryFilterState
  onFilterChange: (filters: HistoryFilterState) => void
}

const INTERVIEW_TYPE_OPTIONS: { value: InterviewType | ''; label: string }[] = [
  { value: '', label: '전체 면접' },
  { value: 'CS', label: 'CS INTERVIEW' },
  { value: 'PORTFOLIO', label: 'PORTFOLIO' },
  { value: 'ALL', label: 'ALL ROUNDER' },
]

const SESSION_STATUS_OPTIONS: { value: SessionStatus | ''; label: string }[] = [
  { value: '', label: '전체 상태' },
  { value: 'COMPLETED', label: '완료' },
  { value: 'IN_PROGRESS', label: '진행중' },
]

const HistoryFilterBar = ({ filters, onFilterChange }: Props) => {
  return (
    <div className="flex flex-col sm:flex-row gap-3">
      <div className="relative flex-1">
        <svg
          className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
        <input
          type="text"
          placeholder="면접 키워드 검색..."
          value={filters.keyword}
          onChange={(e) => onFilterChange({ ...filters, keyword: e.target.value })}
          className="w-full pl-9 pr-4 py-2.5 border border-[#e8e8f0] rounded-lg text-sm text-gray-700 placeholder-gray-400 focus:outline-none focus:border-[#4648d4]"
        />
      </div>

      <select
        value={filters.interviewType}
        onChange={(e) =>
          onFilterChange({ ...filters, interviewType: e.target.value as InterviewType | '' })
        }
        className="px-3 py-2.5 border border-[#e8e8f0] rounded-lg text-sm text-gray-700 bg-white focus:outline-none focus:border-[#4648d4] cursor-pointer"
      >
        {INTERVIEW_TYPE_OPTIONS.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>

      <select
        value={filters.sessionStatus}
        onChange={(e) =>
          onFilterChange({ ...filters, sessionStatus: e.target.value as SessionStatus | '' })
        }
        className="px-3 py-2.5 border border-[#e8e8f0] rounded-lg text-sm text-gray-700 bg-white focus:outline-none focus:border-[#4648d4] cursor-pointer"
      >
        {SESSION_STATUS_OPTIONS.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
    </div>
  )
}

export default HistoryFilterBar
