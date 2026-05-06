const GITHUB_HOST = 'github.com'

export interface GithubRepositoryPath {
  owner: string
  repo: string
  normalizedUrl: string
}

export const parseGithubRepositoryUrl = (rawUrl: string): GithubRepositoryPath | null => {
  let url: URL
  try {
    url = new URL(rawUrl)
  } catch {
    return null
  }

  if (url.protocol !== 'https:' || url.hostname.toLowerCase() !== GITHUB_HOST) {
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
  const response = await fetch(`https://api.github.com/repos/${repository.owner}/${repository.repo}`, {
    headers: {
      Accept: 'application/vnd.github+json',
    },
  })

  if (!response.ok) {
    return false
  }

  const body = (await response.json()) as { private?: boolean }
  return body.private === false
}
