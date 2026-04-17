import { useState } from 'react'
import { ChevronDown, ChevronUp } from 'lucide-react'
import type { ReportQuestionItem } from '../../api/interviewApi'

interface ReportQuestionListItemProps {
  item: ReportQuestionItem
  index: number
}

const ReportQuestionListItem = ({ item, index }: ReportQuestionListItemProps) => {
  const [feedbackOpen, setFeedbackOpen] = useState(false)

  return (
    <div className="bg-white rounded-xl border border-[#c7cbf5] overflow-hidden">
      <div className="px-5 py-4 border-b border-[#eaedff]">
        <div className="flex items-center gap-2 mb-2">
          <span className="text-xs font-medium text-[#4648d4] bg-[#eaedff] px-2 py-0.5 rounded-full">
            Q{index + 1}
          </span>
        </div>
        <p className="text-sm font-medium text-[#131b2e]">{item.question}</p>
      </div>

      {item.answer ? (
        <div className="px-5 py-4">
          <p className="text-xs text-[#767586] mb-1">내 답변</p>
          <p className="text-sm text-[#131b2e] whitespace-pre-wrap">{item.answer}</p>

          {item.feedback && (
            <div className="mt-3">
              <button
                onClick={() => setFeedbackOpen((prev) => !prev)}
                className="flex items-center gap-1 text-xs font-medium text-[#4648d4] hover:underline"
              >
                AI 피드백
                {feedbackOpen ? <ChevronUp size={12} /> : <ChevronDown size={12} />}
              </button>
              {feedbackOpen && (
                <p className="mt-2 text-sm text-[#444] bg-[#faf8ff] border border-[#eaedff] rounded-lg px-4 py-3 whitespace-pre-wrap">
                  {item.feedback}
                </p>
              )}
            </div>
          )}
        </div>
      ) : (
        <div className="px-5 py-4">
          <p className="text-sm text-[#767586] italic">답변 없음</p>
        </div>
      )}
    </div>
  )
}

export default ReportQuestionListItem
