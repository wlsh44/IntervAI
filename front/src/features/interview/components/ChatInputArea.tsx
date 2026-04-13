import { useState } from 'react'
import { Loader2, Send } from 'lucide-react'

interface ChatInputAreaProps {
  onSubmit: (content: string) => void
  isPending: boolean
  disabled?: boolean
}

const ChatInputArea = ({ onSubmit, isPending, disabled = false }: ChatInputAreaProps) => {
  const [value, setValue] = useState('')

  const handleSubmit = () => {
    const trimmed = value.trim()
    if (!trimmed || isPending || disabled) return
    onSubmit(trimmed)
    setValue('')
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSubmit()
    }
  }

  return (
    <div className="border-t border-[#c7cbf5] bg-white px-4 py-3">
      <div className="flex items-end gap-2 bg-[#faf8ff] border border-[#c7cbf5] rounded-xl px-4 py-3">
        <textarea
          value={value}
          onChange={(e) => setValue(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="답변을 입력하세요..."
          aria-label="답변 입력"
          disabled={isPending || disabled}
          rows={3}
          className="flex-1 resize-none bg-transparent text-sm text-[#131b2e] placeholder-[#767586] focus:outline-none disabled:opacity-50"
        />
        <button
          onClick={handleSubmit}
          disabled={isPending || disabled || !value.trim()}
          className="flex items-center justify-center w-9 h-9 bg-[#4648d4] text-white rounded-lg hover:bg-[#3a3bb8] transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex-shrink-0"
        >
          {isPending ? (
            <Loader2 size={16} className="animate-spin" />
          ) : (
            <Send size={16} />
          )}
        </button>
      </div>
    </div>
  )
}

export default ChatInputArea
