import { useState, useCallback, useEffect } from 'react'

type ToastType = 'error' | 'success' | 'info'

interface ToastItem {
  id: number
  message: string
  type: ToastType
}

let toastIdCounter = 0

export const useToast = () => {
  const [toasts, setToasts] = useState<ToastItem[]>([])

  const removeToast = useCallback((id: number) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  const toast = useCallback(
    (message: string, type: ToastType = 'info') => {
      const id = ++toastIdCounter
      setToasts((prev) => [...prev, { id, message, type }])
      setTimeout(() => {
        setToasts((prev) => prev.filter((t) => t.id !== id))
      }, 3000)
    },
    [],
  )

  return { toast, toasts, removeToast }
}

const typeStyles: Record<ToastType, string> = {
  error: 'bg-red-500 text-white',
  success: 'bg-green-500 text-white',
  info: 'bg-gray-800 text-white',
}

interface ToastContainerProps {
  toasts: ToastItem[]
  removeToast: (id: number) => void
}

const ToastItemComponent = ({
  item,
  onRemove,
}: {
  item: ToastItem
  onRemove: () => void
}) => {
  useEffect(() => {
    return () => {}
  }, [])

  return (
    <div
      className={`flex items-center justify-between gap-3 px-4 py-3 rounded-lg shadow-lg min-w-64 max-w-sm ${typeStyles[item.type]}`}
    >
      <span className="text-sm">{item.message}</span>
      <button
        onClick={onRemove}
        className="text-white/80 hover:text-white flex-shrink-0"
        aria-label="닫기"
      >
        x
      </button>
    </div>
  )
}

export const ToastContainer = ({ toasts, removeToast }: ToastContainerProps) => {
  return (
    <div className="fixed bottom-4 right-4 flex flex-col gap-2 z-50">
      {toasts.map((item) => (
        <ToastItemComponent
          key={item.id}
          item={item}
          onRemove={() => removeToast(item.id)}
        />
      ))}
    </div>
  )
}
