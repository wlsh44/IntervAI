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
