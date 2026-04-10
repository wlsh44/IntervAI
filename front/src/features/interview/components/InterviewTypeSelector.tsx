import type { InterviewType } from '../../../shared/types/enums'

interface InterviewTypeSelectorProps {
  value: InterviewType | null
  onChange: (v: InterviewType) => void
  error?: string
}

const OPTIONS: { value: InterviewType; label: string; description: string }[] = [
  { value: 'CS', label: 'CS 지식', description: '자료구조, 알고리즘 등 CS 기초 지식을 평가합니다' },
  { value: 'PORTFOLIO', label: '포트폴리오 기반', description: '프로젝트 경험과 기술 스택을 중심으로 평가합니다' },
  { value: 'ALL', label: '종합 인터뷰', description: 'CS 지식과 포트폴리오를 종합적으로 평가합니다' },
]

const InterviewTypeSelector = ({ value, onChange, error }: InterviewTypeSelectorProps) => {
  return (
    <div>
      <div className="grid grid-cols-3 gap-3">
        {OPTIONS.map((option) => {
          const isSelected = value === option.value
          return (
            <button
              key={option.value}
              type="button"
              onClick={() => onChange(option.value)}
              className={`p-4 rounded-xl text-left transition-colors ${
                isSelected
                  ? 'border-2 border-[#4648d4] bg-[#eaedff]'
                  : 'border border-[#c8c5d4] bg-white hover:border-[#4648d4]'
              }`}
            >
              <p className={`font-semibold text-sm ${isSelected ? 'text-[#4648d4]' : 'text-[#131b2e]'}`}>
                {option.label}
              </p>
              <p className="text-xs text-[#767586] mt-1">{option.description}</p>
            </button>
          )
        })}
      </div>
      {error && <p className="text-xs text-[#ba1a1a] mt-1">{error}</p>}
    </div>
  )
}

export default InterviewTypeSelector
