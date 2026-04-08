import axios from 'axios'
import type { ApiError } from '../types/api'

export function extractApiError(error: unknown): ApiError {
  if (axios.isAxiosError(error) && error.response?.data) {
    return error.response.data as ApiError
  }
  return { code: 'UNKNOWN_ERROR', message: '알 수 없는 오류가 발생했습니다.' }
}

export const ERROR_MESSAGES: Record<string, string> = {
  // 인증
  INVALID_TOKEN: '인증이 만료되었습니다. 다시 로그인해주세요.',
  EXPIRED_TOKEN: '인증이 만료되었습니다. 다시 로그인해주세요.',
  LOGIN_FAILED: '닉네임 또는 비밀번호가 올바르지 않습니다.',
  // 면접
  INTERVIEW_NOT_FOUND: '면접을 찾을 수 없습니다.',
  INTERVIEW_ACCESS_DENIED: '접근 권한이 없습니다.',
  SESSION_NOT_FOUND: '면접 세션을 찾을 수 없습니다.',
  SESSION_ALREADY_COMPLETED: '이미 종료된 면접입니다.',
  ALL_QUESTIONS_ANSWERED: '모든 질문이 완료되었습니다.',
  ANSWER_ALREADY_EXISTS: '이미 답변한 질문입니다.',
  // 공통
  UNKNOWN_ERROR: '알 수 없는 오류가 발생했습니다.',
}

export function getErrorMessage(code: string): string {
  return ERROR_MESSAGES[code] ?? `오류가 발생했습니다. (${code})`
}
