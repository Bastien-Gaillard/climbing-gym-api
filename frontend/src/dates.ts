export function startOfWeek(date: Date): Date {
  const result = new Date(date)
  const day = (result.getDay() + 6) % 7 // monday = 0
  result.setDate(result.getDate() - day)
  result.setHours(0, 0, 0, 0)
  return result
}

export function addDays(date: Date, days: number): Date {
  const result = new Date(date)
  result.setDate(result.getDate() + days)
  return result
}

/** yyyy-MM-dd in local time (toISOString would shift to UTC). */
export function toIsoDate(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

/** yyyy-MM-ddTHH:mm:00 in local time, the format expected by the API (LocalDateTime). */
export function toIsoDateTime(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${toIsoDate(date)}T${pad(date.getHours())}:${pad(date.getMinutes())}:00`
}

export function formatDay(isoDateTime: string): string {
  return new Date(isoDateTime).toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' })
}

export function formatTime(isoDateTime: string): string {
  return new Date(isoDateTime).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
}
