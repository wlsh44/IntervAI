import { useForm, Controller, useWatch } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Loader2 } from 'lucide-react'

import InterviewTypeSelector from './InterviewTypeSelector'
import CsSubjectsSelector from './CsSubjectsSelector'
import TechStackInput from './TechStackInput'
import PortfolioLinkInput from './PortfolioLinkInput'
import DifficultySelector from './DifficultySelector'
import QuestionCountInput from './QuestionCountInput'
import InterviewerToneSelector from './InterviewerToneSelector'
import InterviewSetupSummary from './InterviewSetupSummary'

import { useCreateInterview } from '../hooks/useCreateInterview'
import type { CsSubjectRequest } from '../api/interviewApi'
import type { InterviewType, Difficulty, InterviewerTone } from '../../../shared/types/enums'

const csSubjectSchema = z.object({
  category: z.enum(['DATA_STRUCTURE', 'ALGORITHM', 'NETWORK', 'LANGUAGE', 'DATABASE']),
  topics: z.array(z.string()),
})

const interviewSetupSchema = z
  .object({
    interviewType: z.enum(['CS', 'PORTFOLIO', 'ALL']),
    difficulty: z.enum(['ENTRY', 'JUNIOR', 'SENIOR']),
    questionCount: z.number().int().min(5, '5개 이상').max(10, '10개 이하'),
    interviewerTone: z.enum(['FRIENDLY', 'NORMAL', 'AGGRESSIVE']),
    csSubjects: z.array(csSubjectSchema),
    portfolioLinks: z.array(z.string()),
    techStacks: z.array(z.string()),
  })
  .superRefine((data, ctx) => {
    if ((data.interviewType === 'CS' || data.interviewType === 'ALL') && data.csSubjects.length === 0) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'CS 유형 선택 시 CS 과목을 하나 이상 선택해주세요.',
        path: ['csSubjects'],
      })
    }
    if (
      (data.interviewType === 'PORTFOLIO' || data.interviewType === 'ALL') &&
      data.portfolioLinks.length === 0
    ) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: '포트폴리오 유형 선택 시 포트폴리오 링크를 하나 이상 입력해주세요.',
        path: ['portfolioLinks'],
      })
    }
  })

type InterviewSetupFormValues = z.infer<typeof interviewSetupSchema>

const InterviewSetupForm = () => {
  const { mutate, isPending } = useCreateInterview()

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<InterviewSetupFormValues>({
    resolver: zodResolver(interviewSetupSchema),
    defaultValues: {
      interviewType: 'CS',
      difficulty: 'ENTRY',
      questionCount: 7,
      interviewerTone: 'NORMAL',
      csSubjects: [],
      portfolioLinks: [],
      techStacks: [],
    },
  })

  const watchedValues = useWatch({ control })
  const interviewType = watchedValues.interviewType as InterviewType | undefined

  const showCsSection = interviewType === 'CS' || interviewType === 'ALL'
  const showPortfolioSection = interviewType === 'PORTFOLIO' || interviewType === 'ALL'

  const onSubmit = (data: InterviewSetupFormValues) => {
    const csSubjects: CsSubjectRequest[] | undefined =
      showCsSection
        ? data.csSubjects.filter((s) => s.topics.length > 0)
        : undefined

    mutate({
      interviewType: data.interviewType as InterviewType,
      difficulty: data.difficulty as Difficulty,
      questionCount: data.questionCount,
      interviewerTone: data.interviewerTone as InterviewerTone,
      ...(csSubjects !== undefined && { csSubjects }),
      ...(showPortfolioSection && { portfolioLinks: data.portfolioLinks }),
    })
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="max-w-2xl mx-auto p-8 space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-[#131b2e] mb-2">면접 설정</h1>
        <p className="text-sm text-[#767586]">맞춤형 AI 면접을 시작하기 위해 설정을 선택해주세요.</p>
      </div>

      {/* 면접 유형 */}
      <section className="space-y-3">
        <h2 className="text-base font-semibold text-[#131b2e]">면접 유형</h2>
        <Controller
          control={control}
          name="interviewType"
          render={({ field }) => (
            <InterviewTypeSelector
              value={field.value as InterviewType}
              onChange={field.onChange}
              error={errors.interviewType?.message}
            />
          )}
        />
      </section>

      {/* CS 카테고리 섹션 */}
      {showCsSection && (
        <section className="space-y-3">
          <h2 className="text-base font-semibold text-[#131b2e]">CS 카테고리</h2>
          <Controller
            control={control}
            name="csSubjects"
            render={({ field }) => (
              <CsSubjectsSelector
                value={field.value as CsSubjectRequest[]}
                onChange={field.onChange}
                error={errors.csSubjects?.message}
              />
            )}
          />
        </section>
      )}

      {/* 포트폴리오 섹션 */}
      {showPortfolioSection && (
        <section className="space-y-5">
          <h2 className="text-base font-semibold text-[#131b2e]">포트폴리오 설정</h2>

          <div className="space-y-2">
            <h3 className="text-sm font-medium text-[#464554]">기술 스택</h3>
            <Controller
              control={control}
              name="techStacks"
              render={({ field }) => (
                <TechStackInput
                  value={field.value}
                  onChange={field.onChange}
                  error={errors.techStacks?.message}
                />
              )}
            />
          </div>

          <div className="space-y-2">
            <h3 className="text-sm font-medium text-[#464554]">포트폴리오 링크</h3>
            <Controller
              control={control}
              name="portfolioLinks"
              render={({ field }) => (
                <PortfolioLinkInput
                  value={field.value}
                  onChange={field.onChange}
                  error={errors.portfolioLinks?.message}
                />
              )}
            />
          </div>
        </section>
      )}

      {/* 난이도 */}
      <section className="space-y-3">
        <h2 className="text-base font-semibold text-[#131b2e]">난이도</h2>
        <Controller
          control={control}
          name="difficulty"
          render={({ field }) => (
            <DifficultySelector
              value={field.value as Difficulty}
              onChange={field.onChange}
              error={errors.difficulty?.message}
            />
          )}
        />
      </section>

      {/* 질문 수 */}
      <section className="space-y-3">
        <h2 className="text-base font-semibold text-[#131b2e]">질문 수</h2>
        <Controller
          control={control}
          name="questionCount"
          render={({ field }) => (
            <QuestionCountInput
              value={field.value}
              onChange={field.onChange}
              error={errors.questionCount?.message}
            />
          )}
        />
      </section>

      {/* 면접관 스타일 */}
      <section className="space-y-3">
        <h2 className="text-base font-semibold text-[#131b2e]">면접관 스타일</h2>
        <Controller
          control={control}
          name="interviewerTone"
          render={({ field }) => (
            <InterviewerToneSelector
              value={field.value as InterviewerTone}
              onChange={field.onChange}
              error={errors.interviewerTone?.message}
            />
          )}
        />
      </section>

      {/* 설정 요약 */}
      <InterviewSetupSummary
        formValues={{
          interviewType: (watchedValues.interviewType as InterviewType) ?? null,
          difficulty: (watchedValues.difficulty as Difficulty) ?? null,
          questionCount: watchedValues.questionCount ?? 7,
          interviewerTone: (watchedValues.interviewerTone as InterviewerTone) ?? null,
          csSubjects: (watchedValues.csSubjects as CsSubjectRequest[]) ?? [],
          portfolioLinks: watchedValues.portfolioLinks ?? [],
          techStacks: (watchedValues.techStacks as string[]) ?? [],
        }}
      />

      {/* 제출 버튼 */}
      <button
        type="submit"
        disabled={isPending}
        className="w-full py-3 bg-[#4648d4] text-white font-semibold rounded-xl hover:bg-[#3537b0] transition-colors disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2"
      >
        {isPending && <Loader2 size={18} className="animate-spin" />}
        면접 시작하기
      </button>
    </form>
  )
}

export default InterviewSetupForm
