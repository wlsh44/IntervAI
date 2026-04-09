interface AuthLayoutProps {
  title: string
  children: React.ReactNode
}

const AuthLayout = ({ title, children }: AuthLayoutProps) => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-auth-bg">
      <div className="w-full max-w-md px-6">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold tracking-tight text-white font-[Manrope]">
            Interv<span className="bg-gradient-to-r from-auth-primary to-auth-secondary bg-clip-text text-transparent">AI</span>
          </h1>
          <p className="mt-2 text-sm text-auth-muted">AI 면접 연습 서비스</p>
        </div>

        <div className="bg-auth-card rounded-2xl p-8 shadow-[0_48px_96px_rgba(14,21,48,0.06)]">
          <h2 className="text-xl font-semibold text-auth-text mb-6">{title}</h2>
          {children}
        </div>
      </div>
    </div>
  )
}

export default AuthLayout
