import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import useInterviewHistory from '../hooks/useInterviewHistory'
import useDeleteInterview from '../hooks/useDeleteInterview'
import InterviewHistoryCard from '../components/InterviewHistoryCard'
import HistoryFilterBar from '../components/HistoryFilterBar'
import DeleteConfirmDialog from '../components/DeleteConfirmDialog'
import PaginationBar from '@/shared/components/ui/PaginationBar'
import type { HistoryFilterState, InterviewSummary } from '../types'

const PAGE_SIZE = 10

const HistoryPage = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const currentPage = Number(searchParams.get('page') ?? '0')

  const [filters, setFilters] = useState<HistoryFilterState>({
    keyword: '',
    interviewType: '',
    sessionStatus: '',
  })
  const [deleteTargetId, setDeleteTargetId] = useState<number | null>(null)

  const { data, isLoading, isError } = useInterviewHistory({
    page: currentPage,
    size: PAGE_SIZE,
    interviewType: filters.interviewType || undefined,
    sessionStatus: filters.sessionStatus || undefined,
  })

  const { mutate: doDelete, isPending: isDeleting } = useDeleteInterview(() => {
    setDeleteTargetId(null)
  })

  const handleFilterChange = (next: HistoryFilterState) => {
    setFilters(next)
    setSearchParams({ page: '0' })
  }

  const handlePageChange = (page: number) => {
    setSearchParams({ page: String(page) })
  }

  const filteredContent: InterviewSummary[] = (data?.content ?? []).filter((item) => {
    if (!filters.keyword) return true
    const kw = filters.keyword.toLowerCase()
    return (
      item.interviewType.toLowerCase().includes(kw) ||
      item.difficulty.toLowerCase().includes(kw)
    )
  })

  return (
    <div className="p-8 max-w-3xl">
      <h1 className="text-2xl font-bold text-[#131b2e] mb-1">면접 히스토리</h1>
      <p className="text-sm text-gray-500 mb-6">
        지난 면접 연습 기록을 확인하고 AI 피드백을 통해 부족한 점을 보완하세요.
      </p>

      <div className="mb-6">
        <HistoryFilterBar filters={filters} onFilterChange={handleFilterChange} />
      </div>

      {isLoading && (
        <div className="space-y-4">
          {Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="bg-white rounded-xl border border-[#e8e8f0] p-5 h-28 animate-pulse" />
          ))}
        </div>
      )}

      {isError && (
        <p className="text-sm text-red-500">면접 목록을 불러오는 중 오류가 발생했습니다.</p>
      )}

      {!isLoading && !isError && filteredContent.length === 0 && (
        <p className="text-sm text-gray-400 py-10 text-center">면접 기록이 없습니다.</p>
      )}

      {!isLoading && !isError && filteredContent.length > 0 && (
        <div className="space-y-4">
          {filteredContent.map((interview) => (
            <InterviewHistoryCard
              key={interview.id}
              interview={interview}
              onDelete={(id) => setDeleteTargetId(id)}
            />
          ))}
        </div>
      )}

      {data && (
        <PaginationBar
          currentPage={currentPage}
          totalPages={data.totalPages}
          onPageChange={handlePageChange}
        />
      )}

      <DeleteConfirmDialog
        open={deleteTargetId !== null}
        onConfirm={() => deleteTargetId !== null && doDelete(deleteTargetId)}
        onCancel={() => setDeleteTargetId(null)}
        isPending={isDeleting}
      />
    </div>
  )
}

export default HistoryPage
