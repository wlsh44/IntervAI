import { useEffect, useRef } from 'react'
import AiMessageBubble from './AiMessageBubble'
import CandidateMessageBubble from './CandidateMessageBubble'
import type { ChatMessage } from '../types/chat'

interface ChatMessageListProps {
  messages: ChatMessage[]
  onToggleFeedback: (messageId: string) => void
}

const ChatMessageList = ({ messages, onToggleFeedback }: ChatMessageListProps) => {
  const bottomRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages.length])

  return (
    <div
      className="flex-1 overflow-y-auto px-6 py-4 flex flex-col gap-4 [scrollbar-width:thin] [scrollbar-color:#c7cbf5_#eaedff] [&::-webkit-scrollbar]:w-1.5 [&::-webkit-scrollbar-track]:bg-[#eaedff] [&::-webkit-scrollbar-thumb]:bg-[#c7cbf5] [&::-webkit-scrollbar-thumb]:rounded-full"
    >
      {messages.map((message) =>
        message.role === 'ai' ? (
          <AiMessageBubble
            key={message.id}
            content={message.content}
            questionType={message.questionType}
          />
        ) : (
          <CandidateMessageBubble
            key={message.id}
            content={message.content}
            feedback={message.feedback}
            isFeedbackOpen={message.isFeedbackOpen}
            onToggleFeedback={message.feedback ? () => onToggleFeedback(message.id) : undefined}
          />
        ),
      )}
      <div ref={bottomRef} />
    </div>
  )
}

export default ChatMessageList
