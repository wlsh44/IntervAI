interface QuestionCountInputProps {
  value: number
  onChange: (v: number) => void
  error?: string
}

const MIN = 2
const MAX = 10

const QuestionCountInput = ({ value, onChange, error }: QuestionCountInputProps) => {
  return (
    <div>
      <div className="flex items-center gap-4">
        <button
          type="button"
          onClick={() => onChange(value - 1)}
          disabled={value <= MIN}
          className="w-10 h-10 rounded-lg border border-[#c8c5d4] text-[#4648d4] flex items-center justify-center text-lg font-medium hover:border-[#4648d4] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          aria-label="질문 수 줄이기"
        >
          -
        </button>
        <span className="text-2xl font-bold text-[#131b2e] min-w-8 text-center">{value}</span>
        <button
          type="button"
          onClick={() => onChange(value + 1)}
          disabled={value >= MAX}
          className="w-10 h-10 rounded-lg border border-[#c8c5d4] text-[#4648d4] flex items-center justify-center text-lg font-medium hover:border-[#4648d4] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          aria-label="질문 수 늘리기"
        >
          +
        </button>
        <span className="text-sm text-[#767586]">문제 ({MIN}~{MAX}개)</span>
      </div>
      {error && <p className="text-xs text-[#ba1a1a] mt-1">{error}</p>}
    </div>
  )
}

export default QuestionCountInput
