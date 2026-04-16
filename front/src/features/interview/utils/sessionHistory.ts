import { QuestionType } from '../../../shared/types/enums'
import type { SessionHistoryItem } from '../api/interviewApi'

export interface OrderedSessionHistoryItem extends SessionHistoryItem {
  depth: number
  mainQuestionOrder: number
}

export const orderSessionHistory = (
  history: SessionHistoryItem[],
): OrderedSessionHistoryItem[] => {
  const sortNaturally = (items: SessionHistoryItem[]) =>
    [...items].sort((a, b) => {
      const aIndex = a.questionType === QuestionType.QUESTION ? a.questionIndex : Number.MAX_SAFE_INTEGER
      const bIndex = b.questionType === QuestionType.QUESTION ? b.questionIndex : Number.MAX_SAFE_INTEGER
      const byIndex = aIndex - bIndex
      return byIndex !== 0 ? byIndex : a.questionId - b.questionId
    })

  const byQuestionId = [...history].sort((a, b) => a.questionId - b.questionId)
  const effectiveParentByQuestionId = new Map<number, number | null>()
  let previousByCreationOrder: SessionHistoryItem | null = null

  for (const item of byQuestionId) {
    if (item.parentQuestionId !== null) {
      effectiveParentByQuestionId.set(item.questionId, item.parentQuestionId)
    } else if (item.questionType === QuestionType.FOLLOW_UP && previousByCreationOrder !== null) {
      effectiveParentByQuestionId.set(item.questionId, previousByCreationOrder.questionId)
    } else {
      effectiveParentByQuestionId.set(item.questionId, null)
    }

    previousByCreationOrder = item
  }

  const childrenByParentId = new Map<number, SessionHistoryItem[]>()

  for (const item of history) {
    const effectiveParentQuestionId = effectiveParentByQuestionId.get(item.questionId) ?? null
    if (effectiveParentQuestionId === null) continue
    const children = childrenByParentId.get(effectiveParentQuestionId) ?? []
    children.push(item)
    childrenByParentId.set(effectiveParentQuestionId, children)
  }

  for (const children of childrenByParentId.values()) {
    children.sort((a, b) => a.questionId - b.questionId)
  }

  const rootQuestions = sortNaturally(history)
    .filter((item) => (effectiveParentByQuestionId.get(item.questionId) ?? null) === null)

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

  const remainingItems = sortNaturally(history).filter((item) => !visitedQuestionIds.has(item.questionId))
  for (const item of remainingItems) {
    const nextMainQuestionOrder =
      item.questionType === QuestionType.QUESTION ? mainQuestionOrder + 1 : mainQuestionOrder
    appendDepthFirst(item, 0, nextMainQuestionOrder)
    mainQuestionOrder = nextMainQuestionOrder
  }

  return ordered
}
