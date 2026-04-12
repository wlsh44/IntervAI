import type { InterviewerTone } from '../../../shared/types/enums'

interface InterviewerToneSelectorProps {
  value: InterviewerTone | null
  onChange: (v: InterviewerTone) => void
  error?: string
}

const OPTIONS: { value: InterviewerTone; label: string; description: string }[] = [
  { value: 'FRIENDLY', label: '친절한 면접관', description: '편안한 분위기에서 진행되는 면접입니다' },
  { value: 'NORMAL', label: '일반 면접관', description: '일반적인 면접 분위기로 진행됩니다' },
  { value: 'AGGRESSIVE', label: '압박 면접관', description: '날카로운 질문과 압박적인 분위기의 면접입니다' },
]

const InterviewerToneSelector = ({ value, onChange, error }: InterviewerToneSelectorProps) => {
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

export default InterviewerToneSelector
