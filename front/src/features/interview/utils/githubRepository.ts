import axios from 'axios'

const GITHUB_HOST = 'github.com'
const GITHUB_API_TIMEOUT_MS = 5000

export interface GithubRepositoryPath {
  owner: string
  repo: string
  normalizedUrl: string
}

interface GithubRepositoryResponse {
  private?: boolean
}

export const parseGithubRepositoryUrl = (rawUrl: string): GithubRepositoryPath | null => {
  let url: URL
  try {
    url = new URL(rawUrl)
  } catch {
    return null
  }

  const hostname = url.hostname.toLowerCase()
  if (url.protocol !== 'https:' || (hostname !== GITHUB_HOST && hostname !== `www.${GITHUB_HOST}`)) {
    return null
  }

  const [owner, repoSegment] = url.pathname.split('/').filter(Boolean)
  if (!owner || !repoSegment) {
    return null
  }

  const repo = repoSegment.replace(/\.git$/, '')
  if (!repo) {
    return null
  }

  return {
    owner,
    repo,
    normalizedUrl: `https://github.com/${owner}/${repo}`,
  }
}

export const checkPublicGithubRepository = async (repository: GithubRepositoryPath): Promise<boolean> => {
  try {
    const response = await axios.get<GithubRepositoryResponse>(
      `https://api.github.com/repos/${repository.owner}/${repository.repo}`,
      {
        headers: {
          Accept: 'application/vnd.github+json',
        },
        timeout: GITHUB_API_TIMEOUT_MS,
      },
    )

    return response.data.private === false
  } catch {
    return false
  }
}
