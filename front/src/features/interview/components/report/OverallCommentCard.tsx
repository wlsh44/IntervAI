interface OverallCommentCardProps {
  comment: string
}

const OverallCommentCard = ({ comment }: OverallCommentCardProps) => {
  return (
    <div className="bg-[#f2f3ff] rounded-xl border border-[#c7cbf5] px-5 py-4">
      <p className="text-base font-semibold text-[#131b2e] mb-3">종합 코멘트</p>
      <p className="text-sm text-[#131b2e] leading-relaxed whitespace-pre-wrap">{comment}</p>
    </div>
  )
}

export default OverallCommentCard
