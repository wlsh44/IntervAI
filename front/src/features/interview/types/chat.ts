import type { QuestionType } from '../../../shared/types/enums'

export interface ChatMessage {
  id: string
  role: 'ai' | 'candidate'
  content: string
  questionType?: QuestionType
  feedback?: string
  isFeedbackOpen?: boolean
  questionId?: number
}
