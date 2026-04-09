import { GraduationCap, Briefcase, Brain } from 'lucide-react'
import { CareerLevel } from '../../../shared/types/enums'

interface CareerOption {
  value: CareerLevel
  label: string
  description: string
  Icon: React.ElementType
}

const CAREER_OPTIONS: CareerOption[] = [
  {
    value: 'ENTRY',
    label: '신입',
    description: '0년 경력 이하',
    Icon: GraduationCap,
  },
  {
    value: 'JUNIOR',
    label: '주니어',
    description: '1~3년 경력',
    Icon: Briefcase,
  },
  {
    value: 'SENIOR',
    label: '시니어',
    description: '4년 이상 경력',
    Icon: Brain,
  },
]

interface CareerLevelSelectorProps {
  value: CareerLevel | null
  onChange: (value: CareerLevel) => void
}

const CareerLevelSelector = ({ value, onChange }: CareerLevelSelectorProps) => {
  return (
    <div className="flex gap-3">
      {CAREER_OPTIONS.map(({ value: optionValue, label, description, Icon }) => {
        const isSelected = value === optionValue
        return (
          <button
            key={optionValue}
            type="button"
            onClick={() => onChange(optionValue)}
            className={`flex-1 flex flex-col items-center gap-2 p-4 rounded-xl border-2 transition-colors ${
              isSelected
                ? 'border-[#4648d4] bg-[#eaedff] text-[#4648d4]'
                : 'border-[#767586] bg-white text-[#767586] hover:border-[#4648d4] hover:text-[#4648d4]'
            }`}
          >
            <Icon size={24} />
            <span className="text-sm font-semibold">{label}</span>
            <span className="text-xs">{description}</span>
          </button>
        )
      })}
    </div>
  )
}

export default CareerLevelSelector
