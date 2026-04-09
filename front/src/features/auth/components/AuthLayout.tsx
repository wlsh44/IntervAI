interface AuthLayoutProps {
  title: string
  subtitle: string
  footer: React.ReactNode
  children: React.ReactNode
}

const AuthLayout = ({ title, subtitle, footer, children }: AuthLayoutProps) => {
  return (
    <div
      className="min-h-screen flex flex-col items-center justify-center bg-auth-bg px-6 py-12"
      style={{ fontFamily: 'Inter, sans-serif' }}
    >
      {/* 배경 그라데이션 블러 */}
      <div className="pointer-events-none fixed inset-0 overflow-hidden">
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[700px] h-[350px] rounded-full bg-auth-primary opacity-[0.05] blur-[120px]" />
        <div className="absolute bottom-0 right-1/4 w-[400px] h-[300px] rounded-full bg-auth-secondary opacity-[0.04] blur-[100px]" />
      </div>

      {/* 로고 */}
      <div className="text-center mb-8">
        <h1 className="text-4xl font-bold" style={{ letterSpacing: '-0.02em' }}>
          <span
            className="bg-clip-text text-transparent"
            style={{ backgroundImage: 'linear-gradient(135deg, #4648d4 0%, #6b38d4 100%)' }}
          >
            IntervAI
          </span>
        </h1>
        <p className="mt-1.5 text-xs font-semibold tracking-[0.12em] text-auth-muted uppercase">
          The Intelligent Career Curator
        </p>
      </div>

      {/* 카드 */}
      <div
        className="relative w-full max-w-md rounded-2xl p-8"
        style={{
          background: 'rgba(250, 248, 255, 0.8)',
          backdropFilter: 'blur(24px)',
          boxShadow: '0 20px 40px -12px rgba(70, 72, 212, 0.1), 0 1px 3px rgba(70, 72, 212, 0.06)',
          border: '1px solid rgba(199, 196, 215, 0.3)',
        }}
      >
        {/* 카드 헤더 */}
        <div className="mb-6">
          <h2 className="text-xl font-semibold text-auth-text">{title}</h2>
          <p className="mt-1 text-sm text-auth-muted">{subtitle}</p>
        </div>

        {/* 폼 */}
        {children}

        {/* 카드 하단 링크 */}
        <div className="mt-6 text-center text-sm text-auth-muted">
          {footer}
        </div>
      </div>

      {/* 페이지 하단 저작권 */}
      <p className="mt-8 text-xs text-auth-muted/60">
        © 2024 IntervAI. All rights reserved.
      </p>
    </div>
  )
}

export default AuthLayout
