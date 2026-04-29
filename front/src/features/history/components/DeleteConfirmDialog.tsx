interface Props {
  open: boolean
  onConfirm: () => void
  onCancel: () => void
  isPending: boolean
}

const DeleteConfirmDialog = ({ open, onConfirm, onCancel, isPending }: Props) => {
  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="bg-white rounded-xl shadow-lg p-6 w-80 max-w-full">
        <h2 className="text-base font-semibold text-[#131b2e] mb-3">면접 삭제</h2>
        <p className="text-sm text-[#767586] mb-6">
          삭제된 면접은 복구할 수 없습니다. 정말 삭제하시겠습니까?
        </p>
        <div className="flex gap-2 justify-end">
          <button
            onClick={onCancel}
            disabled={isPending}
            className="px-4 py-2 text-sm font-medium text-[#767586] border border-[#767586] rounded hover:bg-gray-50 transition-colors disabled:opacity-50"
          >
            취소
          </button>
          <button
            onClick={onConfirm}
            disabled={isPending}
            className="px-4 py-2 text-sm font-medium text-white bg-red-500 rounded hover:bg-red-600 transition-colors disabled:opacity-60"
          >
            {isPending ? '삭제 중...' : '삭제'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default DeleteConfirmDialog
