import { useState, type KeyboardEvent } from 'react'
import { X } from 'lucide-react'

const RECOMMENDED_STACKS = [
  'Java',
  'Spring Boot',
  'React',
  'TypeScript',
  'Python',
  'Node.js',
  'MySQL',
  'Docker',
]

const MAX_STACKS = 20

interface TechStackInputProps {
  value: string[]
  onChange: (stacks: string[]) => void
}

const TechStackInput = ({ value, onChange }: TechStackInputProps) => {
  const [inputValue, setInputValue] = useState('')

  const addStack = (stack: string) => {
    const trimmed = stack.trim()
    if (!trimmed) return
    if (value.length >= MAX_STACKS) return
    if (value.includes(trimmed)) return
    onChange([...value, trimmed])
  }

  const removeStack = (stack: string) => {
    onChange(value.filter((s) => s !== stack))
  }

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault()
      addStack(inputValue)
      setInputValue('')
    }
  }

  const handleRecommendClick = (stack: string) => {
    addStack(stack)
  }

  const isMaxReached = value.length >= MAX_STACKS

  return (
    <div className="space-y-3">
      <div className="flex flex-wrap gap-2 min-h-10 p-3 border border-[#767586] rounded-lg bg-white">
        {value.map((stack) => (
          <span
            key={stack}
            className="flex items-center gap-1 px-3 py-1 bg-[#eaedff] text-[#4648d4] rounded-full text-sm font-medium"
          >
            {stack}
            <button
              type="button"
              onClick={() => removeStack(stack)}
              className="hover:text-[#ba1a1a] transition-colors"
              aria-label={`${stack} 삭제`}
            >
              <X size={14} />
            </button>
          </span>
        ))}
        <input
          type="text"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyDown={handleKeyDown}
          disabled={isMaxReached}
          placeholder={isMaxReached ? `최대 ${MAX_STACKS}개까지 입력 가능합니다.` : '기술 스택 입력 후 Enter'}
          className="flex-1 min-w-32 outline-none text-sm text-[#131b2e] placeholder:text-[#767586] disabled:bg-transparent disabled:cursor-not-allowed"
        />
      </div>

      <div>
        <p className="text-xs text-[#767586] mb-2">추천 태그</p>
        <div className="flex flex-wrap gap-2">
          {RECOMMENDED_STACKS.map((stack) => {
            const isAdded = value.includes(stack)
            return (
              <button
                key={stack}
                type="button"
                onClick={() => handleRecommendClick(stack)}
                disabled={isAdded || isMaxReached}
                className={`px-3 py-1 rounded-full text-xs border transition-colors ${
                  isAdded
                    ? 'border-[#4648d4] text-[#4648d4] bg-[#eaedff] cursor-default'
                    : 'border-[#767586] text-[#767586] hover:border-[#4648d4] hover:text-[#4648d4] disabled:opacity-40 disabled:cursor-not-allowed'
                }`}
              >
                {stack}
              </button>
            )
          })}
        </div>
      </div>
    </div>
  )
}

export default TechStackInput
