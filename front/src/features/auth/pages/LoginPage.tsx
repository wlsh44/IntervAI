import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Eye, EyeOff } from 'lucide-react'
import { useLogin } from '../hooks/useLogin'
import { loginSchema, type LoginFormValues } from '../utils/validationSchemas'
import AuthLayout from '../components/AuthLayout'

const LoginPage = () => {
  const [showPassword, setShowPassword] = useState(false)
  const { mutate: login, isPending } = useLogin()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema) })

  const onSubmit = (data: LoginFormValues) => {
    login(data)
  }

  return (
    <AuthLayout title="로그인">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        {/* 닉네임 */}
        <div>
          <label htmlFor="nickname" className="block text-sm text-[#a3aac4] mb-1.5">닉네임</label>
          <input
            id="nickname"
            {...register('nickname')}
            type="text"
            placeholder="닉네임을 입력하세요"
            className="w-full bg-[#141f38] text-[#dee5ff] placeholder-[#40485d] rounded-xl px-4 py-3 text-sm outline-none border border-[#40485d]/20 focus:border-[#85adff]/50 transition-colors"
          />
          {errors.nickname && (
            <p className="mt-1.5 text-xs text-[#ff716c]">{errors.nickname.message}</p>
          )}
        </div>

        {/* 비밀번호 */}
        <div>
          <label htmlFor="password" className="block text-sm text-[#a3aac4] mb-1.5">비밀번호</label>
          <div className="relative">
            <input
              id="password"
              {...register('password')}
              type={showPassword ? 'text' : 'password'}
              placeholder="비밀번호를 입력하세요"
              className="w-full bg-[#141f38] text-[#dee5ff] placeholder-[#40485d] rounded-xl px-4 py-3 pr-11 text-sm outline-none border border-[#40485d]/20 focus:border-[#85adff]/50 transition-colors"
            />
            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-[#a3aac4] hover:text-[#dee5ff] transition-colors"
              aria-label={showPassword ? '비밀번호 숨기기' : '비밀번호 보기'}
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
          {errors.password && (
            <p className="mt-1.5 text-xs text-[#ff716c]">{errors.password.message}</p>
          )}
        </div>

        {/* 로그인 버튼 */}
        <button
          type="submit"
          disabled={isPending}
          className="w-full py-3 rounded-xl text-sm font-semibold text-black bg-gradient-to-r from-[#85adff] to-[#ac8aff] hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed transition-opacity mt-2"
        >
          {isPending ? '로그인 중...' : '로그인'}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-[#a3aac4]">
        계정이 없으신가요?{' '}
        <Link to="/register" className="text-[#85adff] hover:text-[#ac8aff] transition-colors">
          회원가입
        </Link>
      </p>
    </AuthLayout>
  )
}

export default LoginPage
