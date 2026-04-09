import { useState } from 'react'
import { Eye, EyeOff } from 'lucide-react'
import type { UseFormRegisterReturn, FieldError } from 'react-hook-form'

interface PasswordInputProps {
  id: string
  label: string
  registration: UseFormRegisterReturn
  error?: FieldError
  placeholder?: string
  disabled?: boolean
  hint?: string
}

const PasswordInput = ({ id, label, registration, error, placeholder, disabled, hint }: PasswordInputProps) => {
  const [show, setShow] = useState(false)

  return (
    <div>
      <label htmlFor={id} className="block text-sm text-auth-muted mb-1.5">
        {label}
        {hint && <span className="text-auth-outline ml-1">{hint}</span>}
      </label>
      <div className="relative">
        <input
          id={id}
          {...registration}
          type={show ? 'text' : 'password'}
          placeholder={placeholder}
          disabled={disabled}
          className="w-full bg-auth-input text-auth-text placeholder-auth-outline rounded-xl px-4 py-3 pr-11 text-sm outline-none border border-auth-outline/20 focus:border-auth-primary/50 transition-colors disabled:opacity-50"
        />
        <button
          type="button"
          onClick={() => setShow((v) => !v)}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-auth-muted hover:text-auth-text transition-colors"
          aria-label={show ? '비밀번호 숨기기' : '비밀번호 보기'}
        >
          {show ? <EyeOff size={18} /> : <Eye size={18} />}
        </button>
      </div>
      {error && <p className="mt-1.5 text-xs text-auth-error">{error.message}</p>}
    </div>
  )
}

export default PasswordInput
