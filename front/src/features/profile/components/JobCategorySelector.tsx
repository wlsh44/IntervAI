import { JobCategory } from '../../../shared/types/enums'

const JOB_CATEGORY_LABELS: Record<JobCategory, string> = {
  FRONTEND: 'Frontend',
  BACKEND: 'Backend',
  FULLSTACK: 'Fullstack',
  ANDROID: 'Android',
  IOS: 'iOS',
  DEVOPS: 'DevOps',
  DATA_ENGINEER: 'Data Engineer',
  ML_ENGINEER: 'ML Engineer',
}

interface JobCategorySelectorProps {
  value: JobCategory | null
  onChange: (value: JobCategory) => void
  error?: string
}

const JobCategorySelector = ({ value, onChange, error }: JobCategorySelectorProps) => {
  return (
    <div className="space-y-2">
    <div className="flex flex-wrap gap-2">
      {(Object.keys(JobCategory) as JobCategory[]).map((category) => {
        const isSelected = value === category
        return (
          <button
            key={category}
            type="button"
            onClick={() => onChange(category)}
            className={`px-4 py-2 rounded-full text-sm font-medium transition-colors border ${
              isSelected
                ? 'bg-[#4648d4] text-white border-[#4648d4]'
                : 'bg-white text-[#767586] border-[#767586] hover:border-[#4648d4] hover:text-[#4648d4]'
            }`}
          >
            {JOB_CATEGORY_LABELS[category]}
          </button>
        )
      })}
    </div>
    {error && <p className="text-sm text-red-500">{error}</p>}
    </div>
  )
}

export default JobCategorySelector
