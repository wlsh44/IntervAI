import { QuestionType } from '../../../shared/types/enums'
import type { QuestionType as QuestionTypeValue } from '../../../shared/types/enums'

interface AiMessageBubbleProps {
  content: string
  questionType?: QuestionTypeValue
}

const AiMessageBubble = ({ content, questionType }: AiMessageBubbleProps) => {
  const isFollowUp = questionType === QuestionType.FOLLOW_UP

  return (
    <div className="flex flex-col items-start gap-1 max-w-[75%]">
      <div className="flex items-center gap-2">
        <span className="text-xs font-semibold text-[#4648d4]">INTERVAI AI</span>
        {isFollowUp && (
          <span className="bg-[#6b38d4] text-white rounded-full px-3 py-0.5 text-xs font-medium">
            FOLLOW-UP 꼬리 질문
          </span>
        )}
      </div>
      <div className="bg-[#eaedff] text-[#131b2e] rounded-lg rounded-tl-none px-4 py-3 text-sm leading-relaxed">
        {content}
      </div>
    </div>
  )
}

export default AiMessageBubble
