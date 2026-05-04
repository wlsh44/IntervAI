import type { HistoryListParams } from '../api/historyApi'
import { InterviewType, SessionStatus } from '../../../shared/types/enums'

interface HistoryFilterBarProps {
  filters: Omit<HistoryListParams, 'page' | 'size'>
  onChange: (filters: Omit<HistoryListParams, 'page' | 'size'>) => void
}

const HistoryFilterBar = ({ filters, onChange }: HistoryFilterBarProps) => {
  const update = (patch: Partial<typeof filters>) => {
    onChange({ ...filters, ...patch })
  }

  return (
    <div className="flex flex-wrap gap-3 mb-6">
      <input
        type="text"
        placeholder="키워드 검색"
        value={filters.keyword ?? ''}
        onChange={(e) => update({ keyword: e.target.value || undefined })}
        className="border border-[#e2e7ff] rounded-lg px-3 py-2 text-sm text-[#131b2e] placeholder:text-[#131b2e]/40 focus:outline-none focus:ring-2 focus:ring-[#4648d4]/30 w-48"
      />
      <div className="flex items-center gap-2">
        <input
          type="date"
          value={filters.startDate ?? ''}
          onChange={(e) => update({ startDate: e.target.value || undefined })}
          className="border border-[#e2e7ff] rounded-lg px-3 py-2 text-sm text-[#131b2e] focus:outline-none focus:ring-2 focus:ring-[#4648d4]/30"
        />
        <span className="text-[#131b2e]/40 text-sm">~</span>
        <input
          type="date"
          value={filters.endDate ?? ''}
          onChange={(e) => update({ endDate: e.target.value || undefined })}
          className="border border-[#e2e7ff] rounded-lg px-3 py-2 text-sm text-[#131b2e] focus:outline-none focus:ring-2 focus:ring-[#4648d4]/30"
        />
      </div>
      <select
        value={filters.interviewType ?? ''}
        onChange={(e) =>
          update({ interviewType: (e.target.value as InterviewType) || undefined })
        }
        className="border border-[#e2e7ff] rounded-lg px-3 py-2 text-sm text-[#131b2e] focus:outline-none focus:ring-2 focus:ring-[#4648d4]/30 bg-white"
      >
        <option value="">전체 유형</option>
        <option value={InterviewType.CS}>CS 기초</option>
        <option value={InterviewType.PORTFOLIO}>포트폴리오</option>
        <option value={InterviewType.ALL}>종합</option>
      </select>
      <select
        value={filters.status ?? ''}
        onChange={(e) =>
          update({ status: (e.target.value as SessionStatus) || undefined })
        }
        className="border border-[#e2e7ff] rounded-lg px-3 py-2 text-sm text-[#131b2e] focus:outline-none focus:ring-2 focus:ring-[#4648d4]/30 bg-white"
      >
        <option value="">전체 상태</option>
        <option value={SessionStatus.COMPLETED}>완료</option>
        <option value={SessionStatus.IN_PROGRESS}>진행 중</option>
      </select>
    </div>
  )
}

export default HistoryFilterBar
