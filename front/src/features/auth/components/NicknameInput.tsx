import { User } from 'lucide-react'
import type { UseFormRegisterReturn, FieldError } from 'react-hook-form'

interface NicknameInputProps {
  id: string
  label: string
  registration: UseFormRegisterReturn
  error?: FieldError
  placeholder?: string
  disabled?: boolean
  hint?: string
}

const NicknameInput = ({ id, label, registration, error, placeholder, disabled, hint }: NicknameInputProps) => {
  return (
    <div>
      <label htmlFor={id} className="block text-sm font-medium text-auth-text mb-1.5">
        {label}
        {hint && <span className="text-auth-muted font-normal ml-1">{hint}</span>}
      </label>
      <div className="relative">
        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-auth-muted">
          <User size={16} />
        </span>
        <input
          id={id}
          {...registration}
          type="text"
          placeholder={placeholder}
          disabled={disabled}
          className="w-full bg-auth-input text-auth-text placeholder-auth-outline rounded-xl pl-9 pr-4 py-3 text-sm outline-none transition-all disabled:opacity-50 focus:bg-white focus:shadow-[0_0_0_3px_rgba(70,72,212,0.12)]"
        />
      </div>
      {error && <p className="mt-1.5 text-xs text-auth-error">{error.message}</p>}
    </div>
  )
}

export default NicknameInput
