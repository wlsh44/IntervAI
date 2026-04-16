interface FeedbackCardProps {
  feedback: string
  isOpen: boolean
  onToggle: () => void
}

const FeedbackCard = ({ feedback, isOpen, onToggle }: FeedbackCardProps) => {
  return (
    <div className="border-l-4 border-[#00885d] bg-white rounded-r-lg px-4 py-3 mt-2 shadow-sm">
      <button
        onClick={onToggle}
        className="text-xs font-semibold text-[#00885d] hover:underline focus:outline-none"
      >
        {isOpen ? '피드백 숨기기' : '실시간 피드백 보기'}
      </button>
      {isOpen && (
        <p className="mt-2 text-sm text-[#131b2e] leading-relaxed">{feedback}</p>
      )}
    </div>
  )
}

export default FeedbackCard
