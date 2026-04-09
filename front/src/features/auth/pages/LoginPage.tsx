import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { Eye, EyeOff } from 'lucide-react'
import { useLogin } from '../hooks/useLogin'

const schema = z.object({
  nickname: z.string().min(1, '닉네임을 입력해주세요.'),
  password: z.string().min(1, '비밀번호를 입력해주세요.'),
})

type FormValues = z.infer<typeof schema>

const LoginPage = () => {
  const [showPassword, setShowPassword] = useState(false)
  const { mutate: login, isPending } = useLogin()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const onSubmit = (data: FormValues) => {
    login(data)
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#060e20]">
      <div className="w-full max-w-md px-6">
        {/* 로고 */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold tracking-tight text-white font-[Manrope]">
            Interv<span className="bg-gradient-to-r from-[#85adff] to-[#ac8aff] bg-clip-text text-transparent">AI</span>
          </h1>
          <p className="mt-2 text-sm text-[#a3aac4]">AI 면접 연습 서비스</p>
        </div>

        {/* 카드 */}
        <div className="bg-[#0f1930] rounded-2xl p-8 shadow-[0_48px_96px_rgba(14,21,48,0.06)]">
          <h2 className="text-xl font-semibold text-[#dee5ff] mb-6">로그인</h2>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            {/* 닉네임 */}
            <div>
              <label className="block text-sm text-[#a3aac4] mb-1.5">닉네임</label>
              <input
                {...register('nickname')}
                type="text"
                placeholder="닉네임을 입력하세요"
                className="w-full bg-[#141f38] text-[#dee5ff] placeholder-[#40485d] rounded-xl px-4 py-3 text-sm outline-none border border-[#40485d]/20 focus:border-[#85adff]/50 focus:ring-0 transition-colors"
              />
              {errors.nickname && (
                <p className="mt-1.5 text-xs text-[#ff716c]">{errors.nickname.message}</p>
              )}
            </div>

            {/* 비밀번호 */}
            <div>
              <label className="block text-sm text-[#a3aac4] mb-1.5">비밀번호</label>
              <div className="relative">
                <input
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
        </div>
      </div>
    </div>
  )
}

export default LoginPage
