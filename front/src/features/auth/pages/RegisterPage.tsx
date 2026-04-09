import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useRegister } from '../hooks/useRegister'
import { registerSchema, type RegisterFormValues } from '../utils/validationSchemas'
import AuthLayout from '../components/AuthLayout'
import PasswordInput from '../components/PasswordInput'

const RegisterPage = () => {
  const { mutate: registerUser, isPending } = useRegister()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({ resolver: zodResolver(registerSchema) })

  const onSubmit = (data: RegisterFormValues) => {
    registerUser({ nickname: data.nickname, password: data.password })
  }

  return (
    <AuthLayout title="회원가입">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        {/* 닉네임 */}
        <div>
          <label htmlFor="nickname" className="block text-sm text-auth-muted mb-1.5">
            닉네임 <span className="text-auth-outline">(4~8자)</span>
          </label>
          <input
            id="nickname"
            {...register('nickname')}
            type="text"
            placeholder="사용할 닉네임을 입력하세요"
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
          hint="(4~12자)"
          registration={register('password')}
          error={errors.password}
          placeholder="사용할 비밀번호를 입력하세요"
          disabled={isPending}
        />

        {/* 비밀번호 확인 */}
        <PasswordInput
          id="confirmPassword"
          label="비밀번호 확인"
          registration={register('confirmPassword')}
          error={errors.confirmPassword}
          placeholder="비밀번호를 다시 입력하세요"
          disabled={isPending}
        />

        {/* 회원가입 버튼 */}
        <button
          type="submit"
          disabled={isPending}
          className="w-full py-3 rounded-xl text-sm font-semibold text-black bg-gradient-to-r from-auth-primary to-auth-secondary hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed transition-opacity mt-2"
        >
          {isPending ? '가입 중...' : '회원가입'}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-auth-muted">
        이미 계정이 있으신가요?{' '}
        <Link to="/login" className="text-auth-primary hover:text-auth-secondary transition-colors">
          로그인
        </Link>
      </p>
    </AuthLayout>
  )
}

export default RegisterPage
