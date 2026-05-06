import { useState } from 'react'
import { X, Plus, Loader2 } from 'lucide-react'
import { checkPublicGithubRepository, parseGithubRepositoryUrl } from '../utils/githubRepository'

const MAX_LINKS = 5

interface PortfolioLinkInputProps {
  value: string[]
  onChange: (v: string[]) => void
  error?: string
}

const PortfolioLinkInput = ({ value, onChange, error }: PortfolioLinkInputProps) => {
  const [inputValue, setInputValue] = useState('')
  const [urlError, setUrlError] = useState('')
  const [isChecking, setIsChecking] = useState(false)

  const addLink = async () => {
    const trimmed = inputValue.trim()
    if (!trimmed) return

    const repository = parseGithubRepositoryUrl(trimmed)
    if (!repository) {
      setUrlError('GitHub 공개 저장소 URL을 입력해주세요. (예: https://github.com/user/repo)')
      return
    }

    if (value.includes(repository.normalizedUrl)) {
      setUrlError('')
      setInputValue('')
      return
    }

    setIsChecking(true)
    const isPublicRepository = await checkPublicGithubRepository(repository).catch(() => false)
    setIsChecking(false)

    if (!isPublicRepository) {
      setUrlError('접근 가능한 public GitHub 저장소만 등록할 수 있습니다.')
      return
    }

    setInputValue('')
    if (value.length >= MAX_LINKS) return
    onChange([...value, repository.normalizedUrl])
  }

  const removeLink = (link: string) => {
    onChange(value.filter((l) => l !== link))
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      void addLink()
    }
  }

  const isMaxReached = value.length >= MAX_LINKS

  return (
    <div className="space-y-3">
      <div className="flex gap-2">
        <input
          type="url"
          value={inputValue}
          onChange={(e) => { setInputValue(e.target.value); setUrlError('') }}
          onKeyDown={handleKeyDown}
          disabled={isMaxReached || isChecking}
          placeholder="https://github.com/your-project"
          className="flex-1 px-3 py-2 border border-[#767586] rounded-lg text-sm text-[#131b2e] placeholder:text-[#767586] outline-none focus:border-[#4648d4] disabled:bg-gray-50 disabled:cursor-not-allowed"
        />
        <button
          type="button"
          onClick={() => { void addLink() }}
          disabled={isMaxReached || !inputValue.trim() || isChecking}
          className="flex items-center gap-1 px-4 py-2 bg-[#4648d4] text-white rounded-lg text-sm font-medium hover:bg-[#3537b0] transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
        >
          {isChecking ? <Loader2 size={16} className="animate-spin" /> : <Plus size={16} />}
          {isChecking ? '확인 중' : '추가'}
        </button>
      </div>

      {value.length > 0 && (
        <ul className="space-y-2">
          {value.map((link) => (
            <li
              key={link}
              className="flex items-center justify-between gap-2 px-3 py-2 bg-[#eaedff] rounded-lg"
            >
              <a
                href={link}
                target="_blank"
                rel="noopener noreferrer"
                className="text-sm text-[#4648d4] underline truncate"
              >
                {link}
              </a>
              <button
                type="button"
                onClick={() => removeLink(link)}
                className="text-[#767586] hover:text-[#ba1a1a] transition-colors flex-shrink-0"
                aria-label="링크 삭제"
              >
                <X size={16} />
              </button>
            </li>
          ))}
        </ul>
      )}

      {urlError && <p className="text-xs text-[#ba1a1a]">{urlError}</p>}
      <p className="text-xs text-[#767586]">
        {value.length}/{MAX_LINKS}개 등록됨
      </p>
      {error && <p className="text-xs text-[#ba1a1a]">{error}</p>}
    </div>
  )
}

export default PortfolioLinkInput
