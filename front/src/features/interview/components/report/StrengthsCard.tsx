interface StrengthsCardProps {
  items: string[]
}

const StrengthsCard = ({ items }: StrengthsCardProps) => {
  return (
    <div className="bg-white rounded-xl border border-[#c7cbf5] border-l-4 border-l-[#00885d] px-5 py-4">
      <p className="text-base font-semibold text-[#131b2e] mb-3">강점</p>
      <ul className="flex flex-col gap-2">
        {items.map((item, index) => (
          <li key={index} className="flex gap-2 text-sm text-[#131b2e]">
            <span>•</span>
            <span>{item}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default StrengthsCard
