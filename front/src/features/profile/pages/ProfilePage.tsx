import { useEffect } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { JobCategory, CareerLevel } from '../../../shared/types/enums'
import { useProfile } from '../hooks/useProfile'
import { useCreateProfile } from '../hooks/useCreateProfile'
import { useUpdateProfile } from '../hooks/useUpdateProfile'
import { extractApiError } from '../../../shared/api/apiError'
import JobCategorySelector from '../components/JobCategorySelector'
import CareerLevelSelector from '../components/CareerLevelSelector'
import TechStackInput from '../components/TechStackInput'
import PortfolioLinkInput from '../components/PortfolioLinkInput'

const profileSchema = z.object({
  jobCategory: z.enum(
    Object.values(JobCategory) as [JobCategory, ...JobCategory[]],
    { error: '희망 직군을 선택해주세요.' },
  ),
  careerLevel: z.enum(
    Object.values(CareerLevel) as [CareerLevel, ...CareerLevel[]],
    { error: '경력 수준을 선택해주세요.' },
  ),
  techStacks: z
    .array(z.string())
    .min(1, '기술 스택을 1개 이상 입력해주세요.')
    .max(20, '기술 스택은 최대 20개까지 입력 가능합니다.'),
  portfolioLinks: z
    .array(z.string())
    .max(5, '포트폴리오 링크는 최대 5개까지 입력 가능합니다.'),
})

type ProfileFormValues = z.infer<typeof profileSchema>

const ProfilePage = () => {
  const { data: profile, isLoading, error } = useProfile()
  const { mutate: createProfile, isPending: isCreating } = useCreateProfile()
  const { mutate: updateProfile, isPending: isUpdating } = useUpdateProfile()

  const isProfileNotFound =
    error !== null && extractApiError(error).code === 'PROFILE_NOT_FOUND'

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ProfileFormValues>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      jobCategory: undefined,
      careerLevel: undefined,
      techStacks: [],
      portfolioLinks: [],
    },
  })

  useEffect(() => {
    if (isProfileNotFound) {
      createProfile()
    }
  }, [isProfileNotFound, createProfile])

  useEffect(() => {
    if (profile) {
      reset({
        jobCategory: profile.jobCategory ?? undefined,
        careerLevel: profile.careerLevel ?? undefined,
        techStacks: profile.techStacks,
        portfolioLinks: profile.portfolioLinks,
      })
    }
  }, [profile, reset])

  const onSubmit = (data: ProfileFormValues) => {
    updateProfile(data)
  }

  if (isLoading || isCreating) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#faf8ff]">
        <div className="flex flex-col items-center gap-3">
          <div className="w-8 h-8 border-4 border-[#4648d4] border-t-transparent rounded-full animate-spin" />
          <p className="text-sm text-[#767586]">프로필을 불러오는 중...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-[#faf8ff] p-8">
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-[#131b2e]">프로필 설정</h1>
          {profile?.updatedAt && (
            <p className="text-sm text-[#767586] mt-1">
              최근 업데이트: {new Date(profile.updatedAt).toLocaleDateString('ko-KR')}
            </p>
          )}
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
          <div className="bg-[#eaedff] rounded-2xl p-6 space-y-2">
            <label className="text-sm font-semibold text-[#131b2e]">
              희망 직군 <span className="text-[#ba1a1a]">*</span>
            </label>
            <Controller
              name="jobCategory"
              control={control}
              render={({ field }) => (
                <JobCategorySelector
                  value={field.value ?? null}
                  onChange={field.onChange}
                />
              )}
            />
            {errors.jobCategory && (
              <p className="text-xs text-[#ba1a1a]">{errors.jobCategory.message}</p>
            )}
          </div>

          <div className="bg-[#eaedff] rounded-2xl p-6 space-y-2">
            <label className="text-sm font-semibold text-[#131b2e]">경력 수준</label>
            <Controller
              name="careerLevel"
              control={control}
              render={({ field }) => (
                <CareerLevelSelector
                  value={field.value ?? null}
                  onChange={field.onChange}
                />
              )}
            />
            {errors.careerLevel && (
              <p className="text-xs text-[#ba1a1a]">{errors.careerLevel.message}</p>
            )}
          </div>

          <div className="bg-[#eaedff] rounded-2xl p-6 space-y-2">
            <label className="text-sm font-semibold text-[#131b2e]">기술 스택</label>
            <Controller
              name="techStacks"
              control={control}
              render={({ field }) => (
                <TechStackInput value={field.value} onChange={field.onChange} />
              )}
            />
            {errors.techStacks && (
              <p className="text-xs text-[#ba1a1a]">{errors.techStacks.message}</p>
            )}
          </div>

          <div className="bg-[#eaedff] rounded-2xl p-6 space-y-2">
            <label className="text-sm font-semibold text-[#131b2e]">포트폴리오</label>
            <Controller
              name="portfolioLinks"
              control={control}
              render={({ field }) => (
                <PortfolioLinkInput value={field.value} onChange={field.onChange} />
              )}
            />
            {errors.portfolioLinks && (
              <p className="text-xs text-[#ba1a1a]">{errors.portfolioLinks.message}</p>
            )}
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={isUpdating}
              className="flex items-center gap-2 px-8 py-3 bg-[#4648d4] text-white rounded-xl font-semibold hover:bg-[#3537b0] transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {isUpdating ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  저장 중...
                </>
              ) : (
                '저장 및 완료'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default ProfilePage
