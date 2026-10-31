interface Props {
  error: string | null
  onClose: () => void
}

export function ErrorBanner({ error, onClose }: Props) {
  if (!error) return null

  return (
    <div className="banner error" role="alert">
      <span>{error}</span>
      <button className="link" onClick={onClose}>
        Fermer
      </button>
    </div>
  )
}
