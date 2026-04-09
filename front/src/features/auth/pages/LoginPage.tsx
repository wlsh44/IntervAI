import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useLogin } from '../hooks/useLogin'
import { loginSchema, type LoginFormValues } from '../utils/validationSchemas'
import AuthLayout from '../components/AuthLayout'
import NicknameInput from '../components/NicknameInput'
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
    <AuthLayout
      title="로그인"
      subtitle="인터브아이에서 맞춤형 면접 연습을 시작하세요"
      footer={
        <>
          계정이 없으신가요?{' '}
          <Link
            to="/register"
            className="font-medium text-auth-primary hover:text-auth-secondary transition-colors"
          >
            회원가입 하기
          </Link>
        </>
      }
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <NicknameInput
          id="nickname"
          label="닉네임"
          registration={register('nickname')}
          error={errors.nickname}
          placeholder="닉네임을 입력하세요"
          disabled={isPending}
        />

        <PasswordInput
          id="password"
          label="비밀번호"
          registration={register('password')}
          error={errors.password}
          placeholder="비밀번호를 입력하세요"
          disabled={isPending}
        />

        <button
          type="submit"
          disabled={isPending}
          className="w-full py-3 rounded-xl text-sm font-semibold text-white disabled:opacity-50 disabled:cursor-not-allowed transition-opacity hover:opacity-90 mt-2"
          style={{ background: 'linear-gradient(135deg, #4648d4 0%, #6b38d4 100%)' }}
        >
          {isPending ? '로그인 중...' : '로그인'}
        </button>
      </form>
    </AuthLayout>
  )
}

export default LoginPage
