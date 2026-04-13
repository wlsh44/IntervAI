interface ChatHeaderProps {
  currentQuestionIndex: number
  questionCount: number | null
  onOpenDialog: () => void
  isFinishing: boolean
}

const ChatHeader = ({ currentQuestionIndex, questionCount, onOpenDialog, isFinishing }: ChatHeaderProps) => {
  return (
    <header className="bg-[#eaedff] px-6 py-4 flex items-center justify-between border-b border-[#c7cbf5]">
      <h1 className="text-lg font-semibold text-[#131b2e]">AI 면접 세션</h1>

      <div className="flex items-center gap-3">
        {currentQuestionIndex > 0 && (
          <span className="bg-[#eaedff] border border-[#4648d4] text-[#4648d4] text-sm font-medium rounded-full px-3 py-0.5">
            질문 {currentQuestionIndex}/{questionCount ?? '?'}
          </span>
        )}

        <button
          onClick={onOpenDialog}
          disabled={isFinishing}
          className="border border-[#4648d4] text-[#4648d4] text-sm font-medium rounded px-3 py-1.5 hover:bg-[#4648d4] hover:text-white transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          면접 종료
        </button>
      </div>
    </header>
  )
}

export default ChatHeader
