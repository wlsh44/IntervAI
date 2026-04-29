interface Props {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
}

const WINDOW = 2

const PaginationBar = ({ currentPage, totalPages, onPageChange }: Props) => {
  if (totalPages <= 1) return null

  const pages: (number | '...')[] = []

  if (totalPages <= 7) {
    for (let i = 0; i < totalPages; i++) pages.push(i)
  } else {
    pages.push(0)
    if (currentPage > WINDOW + 1) pages.push('...')
    for (let i = Math.max(1, currentPage - WINDOW); i <= Math.min(totalPages - 2, currentPage + WINDOW); i++) {
      pages.push(i)
    }
    if (currentPage < totalPages - WINDOW - 2) pages.push('...')
    pages.push(totalPages - 1)
  }

  return (
    <div className="flex items-center justify-center gap-1 mt-6">
      <button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
        className="px-2 py-1 text-sm text-gray-500 disabled:opacity-30 hover:text-[#4648d4] transition-colors"
        aria-label="이전 페이지"
      >
        &#8249;
      </button>

      {pages.map((p, idx) =>
        p === '...' ? (
          <span key={`ellipsis-${idx}`} className="px-2 py-1 text-sm text-gray-400">
            ...
          </span>
        ) : (
          <button
            key={p}
            onClick={() => onPageChange(p as number)}
            className={`w-8 h-8 rounded-full text-sm font-medium transition-colors ${
              p === currentPage
                ? 'bg-[#4648d4] text-white'
                : 'text-gray-600 hover:text-[#4648d4]'
            }`}
          >
            {(p as number) + 1}
          </button>
        ),
      )}

      <button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
        className="px-2 py-1 text-sm text-gray-500 disabled:opacity-30 hover:text-[#4648d4] transition-colors"
        aria-label="다음 페이지"
      >
        &#8250;
      </button>
    </div>
  )
}

export default PaginationBar
