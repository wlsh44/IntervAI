import useInterviewList from '../../features/dashboard/hooks/useInterviewList'
import WelcomeSection from '../../features/dashboard/components/WelcomeSection'
import StatsSection from '../../features/dashboard/components/StatsSection'
import RecentHistorySection from '../../features/dashboard/components/RecentHistorySection'
import { extractApiError, getErrorMessage } from '../api/apiError'

const DashboardPage = () => {
  const { data, isLoading, isError, error } = useInterviewList()

  if (isLoading) {
    return (
      <div className="bg-[#faf8ff] p-8 space-y-6 min-h-full animate-pulse">
        <div className="bg-[#eaedff] rounded-2xl p-8 h-40" />
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-[#dae2fd] rounded-xl p-5 h-24" />
          <div className="bg-[#dae2fd] rounded-xl p-5 h-24" />
        </div>
        <div className="space-y-3">
          <div className="bg-white rounded-xl border border-[#e2e7ff] p-4 h-16" />
          <div className="bg-white rounded-xl border border-[#e2e7ff] p-4 h-16" />
          <div className="bg-white rounded-xl border border-[#e2e7ff] p-4 h-16" />
        </div>
      </div>
    )
  }

  const apiError = isError ? extractApiError(error) : null

  return (
    <div className="bg-[#faf8ff] p-8 space-y-6 min-h-full">
      <WelcomeSection />
      <StatsSection data={data} />
      {apiError && (
        <p className="text-sm text-red-500">{getErrorMessage(apiError.code)}</p>
      )}
      <RecentHistorySection items={data?.content ?? []} />
    </div>
  )
}

export default DashboardPage
