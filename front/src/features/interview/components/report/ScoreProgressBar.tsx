interface ScoreProgressBarProps {
  label: string
  score: number
}

const ScoreProgressBar = ({ label, score }: ScoreProgressBarProps) => {
  return (
    <div>
      <div className="flex justify-between mb-1">
        <span className="text-sm text-[#131b2e]">{label}</span>
        <span className="text-sm font-medium text-[#4648d4]">{score}점</span>
      </div>
      <div className="bg-[#eaedff] rounded-full h-2">
        <div
          className="bg-[#4648d4] rounded-full h-2"
          style={{ width: `${score}%` }}
        />
      </div>
    </div>
  )
}

export default ScoreProgressBar
