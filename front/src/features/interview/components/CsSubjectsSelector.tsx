import { useState } from 'react'

import type { CsCategory } from '../../../shared/types/enums'
import type { CsSubjectRequest } from '../api/interviewApi'

const CS_TOPICS: Record<CsCategory, string[]> = {
  DATA_STRUCTURE: ['Map', 'List', 'Set', 'Stack', 'Queue', 'Tree', 'Graph'],
  ALGORITHM: ['Sorting', 'Dijkstra', 'DFS/BFS', 'Dynamic Programming'],
  NETWORK: ['HTTP/HTTPS', 'TCP/UDP', 'DNS', 'OSI 7 Layer'],
  LANGUAGE: ['Java', 'Python', 'JavaScript', 'TypeScript', 'Go'],
  DATABASE: ['Index', 'Transaction', 'Join', 'Normalization'],
}

const CATEGORY_LABELS: Record<CsCategory, string> = {
  DATA_STRUCTURE: '자료구조',
  ALGORITHM: '알고리즘',
  NETWORK: '네트워크',
  LANGUAGE: '언어',
  DATABASE: '데이터베이스',
}

const CATEGORIES: CsCategory[] = ['DATA_STRUCTURE', 'ALGORITHM', 'NETWORK', 'LANGUAGE', 'DATABASE']

const splitCustomTopics = (text: string): string[] =>
  text
    .split(',')
    .map((topic) => topic.trim())
    .filter(Boolean)

interface CsSubjectsSelectorProps {
  value: CsSubjectRequest[]
  onChange: (v: CsSubjectRequest[]) => void
  error?: string
}

const CsSubjectsSelector = ({ value, onChange, error }: CsSubjectsSelectorProps) => {
  const [customInputs, setCustomInputs] = useState<Partial<Record<CsCategory, string>>>({})

  const updateCategory = (category: CsCategory, topics: string[]) => {
    const existing = value.find((s) => s.category === category)
    if (existing) {
      onChange(value.map((s) => (s.category === category ? { ...s, topics } : s)))
    } else {
      onChange([...value, { category, topics }])
    }
  }

  const removeCategory = (category: CsCategory) => {
    onChange(value.filter((s) => s.category !== category))
  }

  const clearCustomInput = (category: CsCategory) => {
    setCustomInputs((prev) => {
      const next = { ...prev }
      delete next[category]
      return next
    })
  }

  const getTopicsForCategory = (category: CsCategory): string[] => {
    const found = value.find((s) => s.category === category)
    return found ? found.topics : []
  }

  const isAllSelected = (category: CsCategory): boolean => {
    return value.some((s) => s.category === category && s.topics.length === 0)
  }

  const getCustomTopicsText = (category: CsCategory): string => {
    if (customInputs[category] !== undefined) {
      return customInputs[category] ?? ''
    }

    const selected = getTopicsForCategory(category)
    if (selected.length === 0) return ''
    if (selected.every((topic) => CS_TOPICS[category].includes(topic))) return ''
    return selected.join(', ')
  }

  const toggleTopic = (category: CsCategory, topic: string) => {
    if (isAllSelected(category) || getCustomTopicsText(category)) return

    const currentTopics = getTopicsForCategory(category)
    const isTopicSelected = currentTopics.includes(topic)

    let newTopics: string[]
    if (isTopicSelected) {
      newTopics = currentTopics.filter((t) => t !== topic)
    } else {
      newTopics = [...currentTopics, topic]
    }

    clearCustomInput(category)
    if (newTopics.length === 0) {
      removeCategory(category)
    } else {
      updateCategory(category, newTopics)
    }
  }

  const toggleAll = (category: CsCategory) => {
    clearCustomInput(category)
    if (isAllSelected(category)) {
      removeCategory(category)
    } else {
      updateCategory(category, [])
    }
  }

  const handleCustomTopicsChange = (category: CsCategory, text: string) => {
    setCustomInputs((prev) => ({ ...prev, [category]: text }))

    const customTopics = splitCustomTopics(text)
    if (customTopics.length === 0) {
      removeCategory(category)
      return
    }

    updateCategory(category, customTopics)
  }

  return (
    <div className="space-y-3">
      {CATEGORIES.map((category) => {
        const selectedTopics = getTopicsForCategory(category)
        const allSelected = isAllSelected(category)
        const customTopicsText = getCustomTopicsText(category)
        const hasCustomTopics = customTopicsText.length > 0

        return (
          <div key={category} className="bg-white rounded-xl p-4 border border-[#e2e7ff]">
            <div className="flex items-center justify-between mb-3">
              <span className="font-medium text-sm text-[#131b2e]">{CATEGORY_LABELS[category]}</span>
              <button
                type="button"
                onClick={() => toggleAll(category)}
                className={`text-xs ${allSelected ? 'text-[#ba1a1a]' : 'text-[#4648d4]'} hover:underline`}
              >
                {allSelected ? '전체 해제' : '전체'}
              </button>
            </div>
            <div className="flex flex-wrap gap-2">
              {CS_TOPICS[category].map((topic) => {
                const isSelected = selectedTopics.includes(topic)
                return (
                  <button
                    key={topic}
                    type="button"
                    disabled={allSelected || hasCustomTopics}
                    onClick={() => toggleTopic(category, topic)}
                    className={`px-3 py-1 rounded-full text-xs transition-colors disabled:cursor-not-allowed disabled:opacity-40 ${
                      isSelected
                        ? 'bg-[#4648d4] text-white'
                        : 'border border-[#767586] text-[#767586] hover:border-[#4648d4]'
                    }`}
                  >
                    {topic}
                  </button>
                )
              })}
            </div>
            <input
              type="text"
              value={customTopicsText}
              onChange={(e) => handleCustomTopicsChange(category, e.target.value)}
              disabled={allSelected || (selectedTopics.length > 0 && !hasCustomTopics)}
              placeholder={allSelected ? '전체 범위에서 랜덤 질문 생성' : '직접 입력, 쉼표로 여러 개 구분'}
              className="mt-3 w-full rounded-lg border border-[#d8ddf0] px-3 py-2 text-xs text-[#131b2e] placeholder:text-[#9ca0b5] focus:outline-none focus:ring-2 focus:ring-[#4648d4]/20 disabled:bg-[#f5f6fb] disabled:text-[#767586]"
            />
          </div>
        )
      })}
      {error && <p className="text-xs text-[#ba1a1a] mt-1">{error}</p>}
    </div>
  )
}

export default CsSubjectsSelector
