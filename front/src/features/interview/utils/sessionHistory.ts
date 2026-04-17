import { QuestionType } from '../../../shared/types/enums'
import type { SessionHistoryItem } from '../api/interviewApi'

export interface OrderedSessionHistoryItem extends SessionHistoryItem {
  depth: number
  mainQuestionOrder: number
}

export const orderSessionHistory = (
  history: SessionHistoryItem[],
): OrderedSessionHistoryItem[] => {
  const sortRoots = (items: SessionHistoryItem[]) =>
    [...items].sort((a, b) => {
      const aGroup = a.questionType === QuestionType.QUESTION ? 0 : 1
      const bGroup = b.questionType === QuestionType.QUESTION ? 0 : 1
      if (aGroup !== bGroup) return aGroup - bGroup

      const aIndex = a.questionType === QuestionType.QUESTION ? a.questionIndex : Number.MAX_SAFE_INTEGER
      const bIndex = b.questionType === QuestionType.QUESTION ? b.questionIndex : Number.MAX_SAFE_INTEGER
      const byIndex = aIndex - bIndex
      return byIndex !== 0 ? byIndex : a.questionId - b.questionId
    })

  const effectiveParentByQuestionId = new Map<number, number | null>()
  let previousInResponse: SessionHistoryItem | null = null

  for (const item of history) {
    if (item.parentQuestionId !== null) {
      effectiveParentByQuestionId.set(item.questionId, item.parentQuestionId)
    } else if (item.questionType === QuestionType.FOLLOW_UP && previousInResponse !== null) {
      effectiveParentByQuestionId.set(item.questionId, previousInResponse.questionId)
    } else {
      effectiveParentByQuestionId.set(item.questionId, null)
    }

    previousInResponse = item
  }

  const childrenByParentId = new Map<number, SessionHistoryItem[]>()
  const questionIds = new Set(history.map((item) => item.questionId))

  for (const item of history) {
    const effectiveParentQuestionId = effectiveParentByQuestionId.get(item.questionId) ?? null
    if (effectiveParentQuestionId === null || !questionIds.has(effectiveParentQuestionId)) continue
    const children = childrenByParentId.get(effectiveParentQuestionId) ?? []
    children.push(item)
    childrenByParentId.set(effectiveParentQuestionId, children)
  }

  for (const children of childrenByParentId.values()) {
    children.sort((a, b) => a.questionId - b.questionId)
  }

  const rootQuestions = sortRoots(history).filter((item) => {
    const effectiveParentQuestionId = effectiveParentByQuestionId.get(item.questionId) ?? null
    return effectiveParentQuestionId === null || !questionIds.has(effectiveParentQuestionId)
  })

  const ordered: OrderedSessionHistoryItem[] = []
  const visitedQuestionIds = new Set<number>()

  const appendDepthFirst = (
    item: SessionHistoryItem,
    depth: number,
    mainQuestionOrder: number,
  ) => {
    if (visitedQuestionIds.has(item.questionId)) return
    visitedQuestionIds.add(item.questionId)

    ordered.push({
      ...item,
      depth,
      mainQuestionOrder,
    })

    const children = childrenByParentId.get(item.questionId) ?? []
    for (const child of children) {
      appendDepthFirst(child, depth + 1, mainQuestionOrder)
    }
  }

  let mainQuestionOrder = 0
  for (const root of rootQuestions) {
    const nextMainQuestionOrder =
      root.questionType === QuestionType.QUESTION ? mainQuestionOrder + 1 : mainQuestionOrder
    appendDepthFirst(root, 0, nextMainQuestionOrder)
    mainQuestionOrder = nextMainQuestionOrder
  }

  const remainingItems = history.filter((item) => !visitedQuestionIds.has(item.questionId))
  for (const item of remainingItems) {
    const nextMainQuestionOrder =
      item.questionType === QuestionType.QUESTION ? mainQuestionOrder + 1 : mainQuestionOrder
    appendDepthFirst(item, 0, nextMainQuestionOrder)
    mainQuestionOrder = nextMainQuestionOrder
  }

  return ordered
}
