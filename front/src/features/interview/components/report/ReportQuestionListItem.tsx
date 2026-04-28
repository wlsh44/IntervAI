import { useState } from 'react'
import { ChevronDown, ChevronUp } from 'lucide-react'
import type { FollowUpQuestionItem, ReportQuestionItem } from '../../api/interviewApi'

interface ReportQuestionListItemProps {
  item: ReportQuestionItem
  index: number
}

const FollowUpItem = ({ followUp, parentIndex, followUpIndex }: { followUp: FollowUpQuestionItem; parentIndex: number; followUpIndex: number }) => {
  const [feedbackOpen, setFeedbackOpen] = useState(false)

  return (
    <div className="ml-4 border-l-2 border-[#d7dcff] pl-4">
      <div className="bg-[#faf8ff] rounded-lg border border-[#eaedff] overflow-hidden">
        <div className="px-4 py-3 border-b border-[#eaedff]">
          <span className="text-xs font-medium text-[#767586] bg-[#f2f3ff] px-2 py-0.5 rounded-full">
            Q{parentIndex + 1} 꼬리 {followUpIndex + 1}
          </span>
          <p className="text-sm font-medium text-[#131b2e] mt-2">{followUp.questionContent}</p>
        </div>

        {followUp.answerContent ? (
          <div className="px-4 py-3">
            <p className="text-xs text-[#767586] mb-1">내 답변</p>
            <p className="text-sm text-[#131b2e] whitespace-pre-wrap">{followUp.answerContent}</p>

            {followUp.feedbackContent && (
              <div className="mt-3">
                <button
                  onClick={() => setFeedbackOpen((prev) => !prev)}
                  className="flex items-center gap-1 text-xs font-medium text-[#4648d4] hover:underline"
                >
                  AI 피드백
                  {feedbackOpen ? <ChevronUp size={12} /> : <ChevronDown size={12} />}
                </button>
                {feedbackOpen && (
                  <p className="mt-2 text-sm text-[#444] bg-white border border-[#eaedff] rounded-lg px-4 py-3 whitespace-pre-wrap">
                    {followUp.feedbackContent}
                  </p>
                )}
              </div>
            )}
          </div>
        ) : (
          <div className="px-4 py-3">
            <p className="text-sm text-[#767586] italic">답변 없음</p>
          </div>
        )}
      </div>
    </div>
  )
}

const ReportQuestionListItem = ({ item, index }: ReportQuestionListItemProps) => {
  const [feedbackOpen, setFeedbackOpen] = useState(false)

  return (
    <div className="flex flex-col gap-2">
      <div className="bg-white rounded-xl border border-[#c7cbf5] overflow-hidden">
        <div className="px-5 py-4 border-b border-[#eaedff]">
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-2">
              <span className="text-xs font-medium text-[#4648d4] bg-[#eaedff] px-2 py-0.5 rounded-full">
                Q{index + 1}
              </span>
              {item.score !== null && (
                <span className="text-xs font-medium text-[#00885d] bg-[#e6f5f0] px-2 py-0.5 rounded-full">
                  {item.score}점
                </span>
              )}
            </div>
          </div>
          <p className="text-sm font-medium text-[#131b2e]">{item.questionContent}</p>
          {item.keywords.length > 0 && (
            <div className="flex flex-wrap gap-1 mt-2">
              {item.keywords.map((kw) => (
                <span
                  key={kw}
                  className="text-xs text-[#767586] bg-[#f2f3ff] border border-[#eaedff] px-2 py-0.5 rounded-full"
                >
                  {kw}
                </span>
              ))}
            </div>
          )}
        </div>

        {item.answerContent ? (
          <div className="px-5 py-4">
            <p className="text-xs text-[#767586] mb-1">내 답변</p>
            <p className="text-sm text-[#131b2e] whitespace-pre-wrap">{item.answerContent}</p>

            {item.feedbackContent && (
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
                    {item.feedbackContent}
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

      {item.followUps.length > 0 && (
        <div className="flex flex-col gap-2">
          {item.followUps.map((followUp, i) => (
            <FollowUpItem key={followUp.questionId} followUp={followUp} parentIndex={index} followUpIndex={i} />
          ))}
        </div>
      )}
    </div>
  )
}

export default ReportQuestionListItem
