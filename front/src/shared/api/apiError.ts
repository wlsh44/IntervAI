import axios from 'axios'
import type { ApiError } from '../types/api'

export function extractApiError(error: unknown): ApiError {
  if (axios.isAxiosError(error) && error.response?.data) {
    return error.response.data as ApiError
  }
  return { code: 'UNKNOWN_ERROR', message: '알 수 없는 오류가 발생했습니다.' }
}

export const ApiErrorCode = {
  SESSION_ALREADY_COMPLETED: 'SESSION_ALREADY_COMPLETED',
  ALL_QUESTIONS_ANSWERED: 'ALL_QUESTIONS_ANSWERED',
  ANSWER_ALREADY_EXISTS: 'ANSWER_ALREADY_EXISTS',
  INTERVIEW_NOT_FOUND: 'INTERVIEW_NOT_FOUND',
  INTERVIEW_ACCESS_DENIED: 'INTERVIEW_ACCESS_DENIED',
  SESSION_NOT_FOUND: 'SESSION_NOT_FOUND',
  QUESTION_NOT_FOUND: 'QUESTION_NOT_FOUND',
  SESSION_NOT_COMPLETED: 'SESSION_NOT_COMPLETED',
  REPORT_NOT_FOUND: 'REPORT_NOT_FOUND',
} as const

export const ERROR_MESSAGES: Record<string, string> = {
  // 인증
  INVALID_TOKEN: '인증이 만료되었습니다. 다시 로그인해주세요.',
  EXPIRED_TOKEN: '인증이 만료되었습니다. 다시 로그인해주세요.',
  LOGIN_FAILED: '닉네임 또는 비밀번호가 올바르지 않습니다.',
  DUPLICATE_NICKNAME: '이미 사용 중인 닉네임입니다.',
  // 프로필
  PROFILE_NOT_FOUND: '프로필을 찾을 수 없습니다.',
  PROFILE_ACCESS_DENIED: '본인의 프로필만 수정할 수 있습니다.',
  PROFILE_ALREADY_EXISTS: '이미 프로필이 존재합니다.',
  // 면접
  INVALID_QUESTION_COUNT: '질문 수는 5~10개 사이여야 합니다.',
  CS_SUBJECT_REQUIRED: 'CS 유형 선택 시 CS 과목을 하나 이상 선택해주세요.',
  PORTFOLIO_LINK_REQUIRED: '포트폴리오 유형 선택 시 포트폴리오 링크를 하나 이상 입력해주세요.',
  INTERVIEW_NOT_FOUND: '면접을 찾을 수 없습니다.',
  INTERVIEW_ACCESS_DENIED: '접근 권한이 없습니다.',
  SESSION_NOT_FOUND: '면접 세션을 찾을 수 없습니다.',
  SESSION_ALREADY_COMPLETED: '이미 종료된 면접입니다.',
  ALL_QUESTIONS_ANSWERED: '모든 질문이 완료되었습니다.',
  ANSWER_ALREADY_EXISTS: '이미 답변한 질문입니다.',
  SESSION_NOT_COMPLETED: '면접이 아직 완료되지 않았습니다.',
  REPORT_NOT_FOUND: '리포트를 아직 사용할 수 없습니다.',
  // 공통
  UNKNOWN_ERROR: '알 수 없는 오류가 발생했습니다.',
}

export function getErrorMessage(code: string): string {
  return ERROR_MESSAGES[code] ?? `오류가 발생했습니다. (${code})`
}
