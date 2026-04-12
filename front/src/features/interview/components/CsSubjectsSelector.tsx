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

interface CsSubjectsSelectorProps {
  value: CsSubjectRequest[]
  onChange: (v: CsSubjectRequest[]) => void
  error?: string
}

const CsSubjectsSelector = ({ value, onChange, error }: CsSubjectsSelectorProps) => {
  const getTopicsForCategory = (category: CsCategory): string[] => {
    const found = value.find((s) => s.category === category)
    return found ? found.topics : []
  }

  const isAllSelected = (category: CsCategory): boolean => {
    const selected = getTopicsForCategory(category)
    return selected.length === CS_TOPICS[category].length
  }

  const toggleTopic = (category: CsCategory, topic: string) => {
    const currentTopics = getTopicsForCategory(category)
    const isTopicSelected = currentTopics.includes(topic)

    let newTopics: string[]
    if (isTopicSelected) {
      newTopics = currentTopics.filter((t) => t !== topic)
    } else {
      newTopics = [...currentTopics, topic]
    }

    const existing = value.find((s) => s.category === category)
    if (newTopics.length === 0) {
      onChange(value.filter((s) => s.category !== category))
    } else if (existing) {
      onChange(value.map((s) => (s.category === category ? { ...s, topics: newTopics } : s)))
    } else {
      onChange([...value, { category, topics: newTopics }])
    }
  }

  const toggleAll = (category: CsCategory) => {
    if (isAllSelected(category)) {
      onChange(value.filter((s) => s.category !== category))
    } else {
      const allTopics = CS_TOPICS[category]
      const existing = value.find((s) => s.category === category)
      if (existing) {
        onChange(value.map((s) => (s.category === category ? { ...s, topics: allTopics } : s)))
      } else {
        onChange([...value, { category, topics: allTopics }])
      }
    }
  }

  return (
    <div className="space-y-3">
      {CATEGORIES.map((category) => {
        const selectedTopics = getTopicsForCategory(category)
        const allSelected = isAllSelected(category)

        return (
          <div key={category} className="bg-white rounded-xl p-4 border border-[#e2e7ff]">
            <div className="flex items-center justify-between mb-3">
              <span className="font-medium text-sm text-[#131b2e]">{CATEGORY_LABELS[category]}</span>
              <button
                type="button"
                onClick={() => toggleAll(category)}
                className="text-xs text-[#4648d4] hover:underline"
              >
                {allSelected ? '전체 해제' : '전체 선택'}
              </button>
            </div>
            <div className="flex flex-wrap gap-2">
              {CS_TOPICS[category].map((topic) => {
                const isSelected = selectedTopics.includes(topic)
                return (
                  <button
                    key={topic}
                    type="button"
                    onClick={() => toggleTopic(category, topic)}
                    className={`px-3 py-1 rounded-full text-xs transition-colors ${
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
          </div>
        )
      })}
      {error && <p className="text-xs text-[#ba1a1a] mt-1">{error}</p>}
    </div>
  )
}

export default CsSubjectsSelector
