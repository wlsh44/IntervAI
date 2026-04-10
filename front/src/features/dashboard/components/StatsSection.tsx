import type { InterviewListResponse } from '../api/dashboardApi'

interface StatsSectionProps {
  data: InterviewListResponse | undefined
}

const formatDate = (createdAt: string): string => {
  return new Date(createdAt).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

const StatsSection = ({ data }: StatsSectionProps) => {
  const totalCount = data?.totalElements ?? 0
  const recentDate = data?.content[0]?.createdAt ? formatDate(data.content[0].createdAt) : '-'

  return (
    <div className="grid grid-cols-2 gap-4">
      <div className="bg-[#dae2fd] rounded-xl p-5">
        <p className="text-2xl font-bold text-[#131b2e]">{totalCount}회</p>
        <p className="text-sm text-[#131b2e]/60 mt-1">총 면접 횟수</p>
      </div>
      <div className="bg-[#dae2fd] rounded-xl p-5">
        <p className="text-2xl font-bold text-[#131b2e]">{recentDate}</p>
        <p className="text-sm text-[#131b2e]/60 mt-1">최근 면접일</p>
      </div>
    </div>
  )
}

export default StatsSection
