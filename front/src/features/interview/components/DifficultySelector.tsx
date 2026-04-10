import type { Difficulty } from '../../../shared/types/enums'

interface DifficultySelectorProps {
  value: Difficulty | null
  onChange: (v: Difficulty) => void
  error?: string
}

const OPTIONS: { value: Difficulty; label: string; description: string }[] = [
  { value: 'ENTRY', label: '신입', description: '기초 개념과 학습 의지를 중심으로 평가합니다' },
  { value: 'JUNIOR', label: '주니어', description: '실무 경험과 문제 해결 능력을 평가합니다' },
  { value: 'SENIOR', label: '시니어', description: '깊이 있는 전문 지식과 설계 능력을 평가합니다' },
]

const DifficultySelector = ({ value, onChange, error }: DifficultySelectorProps) => {
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

export default DifficultySelector
