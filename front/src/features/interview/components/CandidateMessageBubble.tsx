import FeedbackCard from './FeedbackCard'

interface CandidateMessageBubbleProps {
  content: string
  feedback?: string
  isFeedbackOpen?: boolean
  onToggleFeedback?: () => void
}

const CandidateMessageBubble = ({
  content,
  feedback,
  isFeedbackOpen = false,
  onToggleFeedback,
}: CandidateMessageBubbleProps) => {
  return (
    <div className="flex flex-col items-end gap-1 max-w-[75%] self-end">
      <span className="text-xs font-semibold text-[#767586]">CANDIDATE</span>
      <div className="bg-[#4648d4] text-white rounded-lg rounded-tr-none px-4 py-3 text-sm leading-relaxed">
        {content}
      </div>
      {feedback && onToggleFeedback && (
        <FeedbackCard
          feedback={feedback}
          isOpen={isFeedbackOpen}
          onToggle={onToggleFeedback}
        />
      )}
    </div>
  )
}

export default CandidateMessageBubble
