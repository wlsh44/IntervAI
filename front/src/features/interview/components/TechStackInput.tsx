import { useState, type KeyboardEvent } from 'react'
import { X } from 'lucide-react'

const MAX_STACKS = 20

interface TechStackInputProps {
  value: string[]
  onChange: (v: string[]) => void
  error?: string
}

const TechStackInput = ({ value, onChange, error }: TechStackInputProps) => {
  const [inputValue, setInputValue] = useState('')

  const removeStack = (stack: string) => {
    onChange(value.filter((s) => s !== stack))
  }

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault()
      const newStacks = inputValue
        .split(/[,\n]/)
        .map((s) => s.trim())
        .filter((s) => s && !value.includes(s))

      if (newStacks.length > 0) {
        onChange([...value, ...newStacks].slice(0, MAX_STACKS))
      }
      setInputValue('')
    }
  }

  const isMaxReached = value.length >= MAX_STACKS

  return (
    <div className="space-y-2">
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
      {error && <p className="text-xs text-[#ba1a1a] mt-1">{error}</p>}
    </div>
  )
}

export default TechStackInput
