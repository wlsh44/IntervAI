import { useState } from 'react'
import useInterviewHistory from '../../features/history/hooks/useInterviewHistory'
import useDeleteInterview from '../../features/history/hooks/useDeleteInterview'
import HistoryFilterBar from '../../features/history/components/HistoryFilterBar'
import HistoryCard from '../../features/history/components/HistoryCard'
import type { HistoryListParams } from '../../features/history/api/historyApi'

type Filters = Omit<HistoryListParams, 'page' | 'size'>

const HistoryPage = () => {
  const [filters, setFilters] = useState<Filters>({})
  const [page, setPage] = useState(0)

  const { data, isLoading, isError } = useInterviewHistory(filters, page)
  const { mutate: deleteInterview, isPending: isDeleting } = useDeleteInterview()

  const totalPages = data?.totalPages ?? 0

  const handleFiltersChange = (newFilters: Filters) => {
    setFilters(newFilters)
    setPage(0)
  }

  const handleDelete = (id: number) => {
    deleteInterview(id, {
      onSuccess: () => {
        if (data && data.content.length === 1 && page > 0) {
          setPage((p) => p - 1)
        }
      },
    })
  }

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <h1 className="text-[#131b2e] font-bold text-2xl mb-6">면접 기록</h1>

      <HistoryFilterBar filters={filters} onChange={handleFiltersChange} />

      {isLoading ? (
        <p className="text-[#131b2e]/40 text-center py-12">불러오는 중...</p>
      ) : isError ? (
        <p className="text-red-400 text-center py-12">데이터를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.</p>
      ) : !data || data.content.length === 0 ? (
        <p className="text-[#131b2e]/40 text-center py-12">면접 기록이 없습니다</p>
      ) : (
        <ul className="space-y-3">
          {data.content.map((item) => (
            <HistoryCard
              key={item.id}
              item={item}
              onDelete={handleDelete}
              isDeleting={isDeleting}
            />
          ))}
        </ul>
      )}

      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-2 mt-8">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            className="px-3 py-1.5 rounded-lg border border-[#e2e7ff] text-sm text-[#131b2e]/60 hover:bg-[#faf8ff] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          >
            이전
          </button>
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={() => setPage(i)}
              className={`w-8 h-8 rounded-lg text-sm font-medium transition-colors ${
                i === page
                  ? 'bg-[#4648d4] text-white'
                  : 'border border-[#e2e7ff] text-[#131b2e]/60 hover:bg-[#faf8ff]'
              }`}
            >
              {i + 1}
            </button>
          ))}
          <button
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page === totalPages - 1}
            className="px-3 py-1.5 rounded-lg border border-[#e2e7ff] text-sm text-[#131b2e]/60 hover:bg-[#faf8ff] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          >
            다음
          </button>
        </div>
      )}
    </div>
  )
}

export default HistoryPage
