interface AllQuestionsCompletedBannerProps {
  onOpenDialog: () => void
  isFinishing: boolean
}

const AllQuestionsCompletedBanner = ({ onOpenDialog, isFinishing }: AllQuestionsCompletedBannerProps) => {
  return (
    <div className="flex flex-col items-center justify-center gap-4 py-6 px-4 bg-[#eaedff] rounded-xl mx-4">
      <p className="text-sm font-medium text-[#131b2e] text-center">
        모든 질문이 완료되었습니다. 면접을 종료해주세요.
      </p>
      <button
        onClick={onOpenDialog}
        disabled={isFinishing}
        className="bg-[#4648d4] text-white text-sm font-medium px-6 py-2 rounded-lg hover:bg-[#3a3bb8] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {isFinishing ? '종료 중...' : '면접 종료'}
      </button>
    </div>
  )
}

export default AllQuestionsCompletedBanner
