interface FinishConfirmDialogProps {
  isOpen: boolean
  onConfirm: () => void
  onCancel: () => void
}

const FinishConfirmDialog = ({ isOpen, onConfirm, onCancel }: FinishConfirmDialogProps) => {
  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="bg-white rounded-xl shadow-lg p-6 w-80 max-w-full">
        <h2 className="text-base font-semibold text-[#131b2e] mb-3">면접 종료</h2>
        <p className="text-sm text-[#767586] mb-6">
          종료하면 현재까지의 면접만 저장됩니다. 종료하시겠습니까?
        </p>
        <div className="flex gap-2 justify-end">
          <button
            onClick={onCancel}
            className="px-4 py-2 text-sm font-medium text-[#767586] border border-[#767586] rounded hover:bg-gray-50 transition-colors"
          >
            취소
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-2 text-sm font-medium text-white bg-[#4648d4] rounded hover:bg-[#3a3bb8] transition-colors"
          >
            종료
          </button>
        </div>
      </div>
    </div>
  )
}

export default FinishConfirmDialog
