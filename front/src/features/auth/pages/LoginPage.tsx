import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useLogin } from '../hooks/useLogin'
import { loginSchema, type LoginFormValues } from '../utils/validationSchemas'
import AuthLayout from '../components/AuthLayout'
import PasswordInput from '../components/PasswordInput'

const LoginPage = () => {
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
          <label htmlFor="nickname" className="block text-sm text-auth-muted mb-1.5">닉네임</label>
          <input
            id="nickname"
            {...register('nickname')}
            type="text"
            placeholder="닉네임을 입력하세요"
            disabled={isPending}
            className="w-full bg-auth-input text-auth-text placeholder-auth-outline rounded-xl px-4 py-3 text-sm outline-none border border-auth-outline/20 focus:border-auth-primary/50 transition-colors disabled:opacity-50"
          />
          {errors.nickname && (
            <p className="mt-1.5 text-xs text-auth-error">{errors.nickname.message}</p>
          )}
        </div>

        {/* 비밀번호 */}
        <PasswordInput
          id="password"
          label="비밀번호"
          registration={register('password')}
          error={errors.password}
          placeholder="비밀번호를 입력하세요"
          disabled={isPending}
        />

        {/* 로그인 버튼 */}
        <button
          type="submit"
          disabled={isPending}
          className="w-full py-3 rounded-xl text-sm font-semibold text-black bg-gradient-to-r from-auth-primary to-auth-secondary hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed transition-opacity mt-2"
        >
          {isPending ? '로그인 중...' : '로그인'}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-auth-muted">
        계정이 없으신가요?{' '}
        <Link to="/register" className="text-auth-primary hover:text-auth-secondary transition-colors">
          회원가입
        </Link>
      </p>
    </AuthLayout>
  )
}

export default LoginPage
