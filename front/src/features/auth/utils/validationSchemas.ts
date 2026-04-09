import { z } from 'zod'

export const loginSchema = z.object({
  nickname: z.string().min(1, '닉네임을 입력해주세요.'),
  password: z.string().min(1, '비밀번호를 입력해주세요.'),
})

export const registerSchema = z.object({
  nickname: z
    .string()
    .min(4, '닉네임은 4자 이상 입력해주세요.')
    .max(8, '닉네임은 8자 이하로 입력해주세요.'),
  password: z
    .string()
    .min(4, '비밀번호는 4자 이상 입력해주세요.')
    .max(12, '비밀번호는 12자 이하로 입력해주세요.'),
})

export type LoginFormValues = z.infer<typeof loginSchema>
export type RegisterFormValues = z.infer<typeof registerSchema>
