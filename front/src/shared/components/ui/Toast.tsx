import { create } from 'zustand'

type ToastType = 'error' | 'success' | 'info'

interface ToastItem {
  id: number
  message: string
  type: ToastType
}

interface ToastState {
  toasts: ToastItem[]
  toast: (message: string, type?: ToastType) => void
  removeToast: (id: number) => void
}

let toastIdCounter = 0

export const useToastStore = create<ToastState>((set) => ({
  toasts: [],
  toast: (message, type = 'info') => {
    const id = ++toastIdCounter
    set((state) => ({ toasts: [...state.toasts, { id, message, type }] }))
    setTimeout(() => {
      set((state) => ({ toasts: state.toasts.filter((t) => t.id !== id) }))
    }, 3000)
  },
  removeToast: (id) =>
    set((state) => ({ toasts: state.toasts.filter((t) => t.id !== id) })),
}))

export const useToast = () => {
  const { toast } = useToastStore()
  return { toast }
}

const typeStyles: Record<ToastType, string> = {
  error: 'bg-red-500 text-white',
  success: 'bg-green-500 text-white',
  info: 'bg-gray-800 text-white',
}

export const ToastContainer = () => {
  const { toasts, removeToast } = useToastStore()

  return (
    <div className="fixed bottom-4 right-4 flex flex-col gap-2 z-50">
      {toasts.map((item) => (
        <div
          key={item.id}
          className={`flex items-center justify-between gap-3 px-4 py-3 rounded-lg shadow-lg min-w-64 max-w-sm ${typeStyles[item.type]}`}
        >
          <span className="text-sm">{item.message}</span>
          <button
            onClick={() => removeToast(item.id)}
            className="text-white/80 hover:text-white flex-shrink-0"
            aria-label="닫기"
          >
            ✕
          </button>
        </div>
      ))}
    </div>
  )
}
