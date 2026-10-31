import { useCallback, useEffect, useState } from 'react'
import { ApiError } from './api/client'

export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) return `${error.message} (${error.code})`
  return 'Erreur inattendue'
}

/** Loads data on mount and whenever `deps` change; `reload` refetches on demand. */
export function useApi<T>(load: () => Promise<T>, deps: unknown[]) {
  const [data, setData] = useState<T | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const reload = useCallback(load, deps)

  const refresh = useCallback(async () => {
    setLoading(true)
    try {
      setData(await reload())
      setError(null)
    } catch (e) {
      setError(errorMessage(e))
    } finally {
      setLoading(false)
    }
  }, [reload])

  useEffect(() => {
    void refresh()
  }, [refresh])

  return { data, error, loading, refresh, setError }
}
