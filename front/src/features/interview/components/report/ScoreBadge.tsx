interface ScoreBadgeProps {
  score: number
}

const ScoreBadge = ({ score }: ScoreBadgeProps) => {
  return (
    <div className="bg-[#f2f3ff] rounded-xl px-8 py-6 flex flex-col items-center">
      <p className="text-sm text-[#767586] mb-2">종합 점수</p>
      <p className="text-5xl font-bold text-[#4648d4]">
        {score}<span className="text-2xl ml-1">점</span>
      </p>
    </div>
  )
}

export default ScoreBadge
