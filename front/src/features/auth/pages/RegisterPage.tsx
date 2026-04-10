import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useRegister } from '../hooks/useRegister'
import { registerSchema, type RegisterFormValues } from '../utils/validationSchemas'
import AuthLayout from '../components/AuthLayout'
import NicknameInput from '../components/NicknameInput'
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
    <AuthLayout
      title="회원가입"
      subtitle="인터브아이에서 AI 면접 연습을 시작하세요"
      footer={
        <>
          이미 계정이 있으신가요?{' '}
          <Link
            to="/login"
            className="font-medium text-auth-primary hover:text-auth-secondary transition-colors"
          >
            로그인
          </Link>
        </>
      }
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <NicknameInput
          id="nickname"
          label="닉네임"
          hint="(4~8자)"
          registration={register('nickname')}
          error={errors.nickname}
          placeholder="사용할 닉네임을 입력하세요"
          disabled={isPending}
        />

        <PasswordInput
          id="password"
          label="비밀번호"
          hint="(4~12자)"
          registration={register('password')}
          error={errors.password}
          placeholder="사용할 비밀번호를 입력하세요"
          disabled={isPending}
        />

        <PasswordInput
          id="confirmPassword"
          label="비밀번호 확인"
          registration={register('confirmPassword')}
          error={errors.confirmPassword}
          placeholder="비밀번호를 다시 입력하세요"
          disabled={isPending}
        />

        <button
          type="submit"
          disabled={isPending}
          className="w-full py-3 rounded-xl text-sm font-semibold text-white disabled:opacity-50 disabled:cursor-not-allowed transition-opacity hover:opacity-90 mt-2"
          style={{ background: 'linear-gradient(135deg, #4648d4 0%, #6b38d4 100%)' }}
        >
          {isPending ? '가입 중...' : '회원가입'}
        </button>
      </form>
    </AuthLayout>
  )
}

export default RegisterPage
