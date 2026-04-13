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
  }, [messages])

  return (
    <div
      className="flex-1 overflow-y-auto px-6 py-4 flex flex-col gap-4"
      style={{
        scrollbarWidth: 'thin',
        scrollbarColor: '#c7cbf5 #eaedff',
      }}
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
