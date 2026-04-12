import type { InterviewType, Difficulty, InterviewerTone } from '../../../shared/types/enums'
import type { CsSubjectRequest } from '../api/interviewApi'

interface FormValues {
  interviewType: InterviewType | null
  difficulty: Difficulty | null
  questionCount: number
  interviewerTone: InterviewerTone | null
  csSubjects: CsSubjectRequest[]
  portfolioLinks: string[]
  techStacks: string[]
}

interface InterviewSetupSummaryProps {
  formValues: FormValues
}

const INTERVIEW_TYPE_LABELS: Record<InterviewType, string> = {
  CS: 'CS 지식',
  PORTFOLIO: '포트폴리오 기반',
  ALL: '종합 인터뷰',
}

const DIFFICULTY_LABELS: Record<Difficulty, string> = {
  ENTRY: '신입',
  JUNIOR: '주니어',
  SENIOR: '시니어',
}

const TONE_LABELS: Record<InterviewerTone, string> = {
  FRIENDLY: '친절한 면접관',
  NORMAL: '일반 면접관',
  AGGRESSIVE: '압박 면접관',
}

const InterviewSetupSummary = ({ formValues }: InterviewSetupSummaryProps) => {
  const { interviewType, difficulty, questionCount, interviewerTone, csSubjects, portfolioLinks, techStacks } = formValues

  const totalCsTopics = csSubjects.reduce((sum, s) => sum + s.topics.length, 0)
  const showCsSection = interviewType === 'CS' || interviewType === 'ALL'
  const showPortfolioSection = interviewType === 'PORTFOLIO' || interviewType === 'ALL'

  const rows: { label: string; value: string }[] = [
    { label: '면접 유형', value: interviewType ? INTERVIEW_TYPE_LABELS[interviewType] : '-' },
    { label: '난이도', value: difficulty ? DIFFICULTY_LABELS[difficulty] : '-' },
    { label: '질문 수', value: `${questionCount}문제` },
    { label: '면접관 스타일', value: interviewerTone ? TONE_LABELS[interviewerTone] : '-' },
    ...(showCsSection ? [{ label: 'CS 과목 수', value: `${totalCsTopics}개` }] : []),
    ...(showPortfolioSection
      ? [
          { label: '기술 스택 수', value: `${techStacks.length}개` },
          { label: '포트폴리오 링크 수', value: `${portfolioLinks.length}개` },
        ]
      : []),
  ]

  return (
    <div className="bg-[#eaedff] rounded-xl p-5">
      <h3 className="font-semibold text-sm text-[#131b2e] mb-3">설정 요약</h3>
      <div className="space-y-2">
        {rows.map((row) => (
          <div key={row.label} className="flex justify-between items-center">
            <span className="text-sm text-[#464554]">{row.label}</span>
            <span className="text-sm font-medium text-[#131b2e]">{row.value}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

export default InterviewSetupSummary
