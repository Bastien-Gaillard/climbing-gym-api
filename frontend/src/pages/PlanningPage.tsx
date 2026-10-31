import { useMemo, useState, type FormEvent } from 'react'
import { api } from '../api/client'
import type { Member, Slot, SlotType } from '../api/types'
import { ErrorBanner } from '../components/ErrorBanner'
import { addDays, formatDay, formatTime, startOfWeek, toIsoDate, toIsoDateTime } from '../dates'
import { errorMessage, useApi } from '../hooks'
import { SLOT_LABELS } from '../labels'

export function PlanningPage() {
  const [weekStart, setWeekStart] = useState(() => startOfWeek(new Date()))
  const [memberId, setMemberId] = useState('')
  const [notice, setNotice] = useState<string | null>(null)

  const from = toIsoDate(weekStart)
  const to = toIsoDate(addDays(weekStart, 6))

  const slots = useApi(() => api.get<Slot[]>(`/api/slots?from=${from}&to=${to}`), [from, to])
  const members = useApi(() => api.get<Member[]>('/api/members'), [])

  const slotsByDay = useMemo(() => {
    const groups = new Map<string, Slot[]>()
    for (const slot of slots.data ?? []) {
      const day = slot.startsAt.slice(0, 10)
      groups.set(day, [...(groups.get(day) ?? []), slot])
    }
    return [...groups.entries()]
  }, [slots.data])

  async function book(slot: Slot) {
    if (!memberId) return slots.setError('Choisis un adhérent avant de réserver')
    try {
      await api.post(`/api/slots/${slot.id}/bookings`, { memberId })
      setNotice(`Réservé : ${SLOT_LABELS[slot.type]} du ${formatDay(slot.startsAt)} à ${formatTime(slot.startsAt)}`)
      await slots.refresh()
    } catch (e) {
      slots.setError(errorMessage(e))
    }
  }

  return (
    <section>
      <div className="toolbar">
        <button onClick={() => setWeekStart(addDays(weekStart, -7))}>← Semaine précédente</button>
        <strong>
          Semaine du {weekStart.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long' })}
        </strong>
        <button onClick={() => setWeekStart(addDays(weekStart, 7))}>Semaine suivante →</button>
      </div>

      <label className="field inline">
        Réserver pour
        <select value={memberId} onChange={(e) => setMemberId(e.target.value)}>
          <option value="">— adhérent —</option>
          {(members.data ?? []).map((m) => (
            <option key={m.id} value={m.id}>
              {m.lastName} {m.firstName}
            </option>
          ))}
        </select>
      </label>

      <ErrorBanner error={slots.error} onClose={() => slots.setError(null)} />
      {notice && (
        <div className="banner success">
          <span>{notice}</span>
          <button className="link" onClick={() => setNotice(null)}>
            Fermer
          </button>
        </div>
      )}

      {slots.loading && <p className="muted">Chargement…</p>}
      {!slots.loading && slotsByDay.length === 0 && <p className="muted">Aucun créneau cette semaine.</p>}

      {slotsByDay.map(([day, daySlots]) => (
        <div key={day} className="card">
          <h3 className="day">{formatDay(daySlots[0].startsAt)}</h3>
          <ul className="slots">
            {daySlots.map((slot) => (
              <li key={slot.id} className="slot">
                <span className={`badge ${slot.type.toLowerCase()}`}>{SLOT_LABELS[slot.type]}</span>
                <span>
                  {formatTime(slot.startsAt)} – {formatTime(slot.endsAt)}
                </span>
                <span className={slot.remainingPlaces === 0 ? 'full' : 'muted'}>
                  {slot.remainingPlaces === 0 ? 'Complet' : `${slot.remainingPlaces} / ${slot.capacity} places`}
                </span>
                <button disabled={slot.remainingPlaces === 0} onClick={() => book(slot)}>
                  Réserver
                </button>
              </li>
            ))}
          </ul>
        </div>
      ))}

      <NewSlotForm onCreated={slots.refresh} />
    </section>
  )
}

function NewSlotForm({ onCreated }: { onCreated: () => Promise<void> }) {
  const [type, setType] = useState<SlotType>('BOULDER')
  const [startsAt, setStartsAt] = useState('')
  const [durationMinutes, setDurationMinutes] = useState(120)
  const [capacity, setCapacity] = useState(20)
  const [error, setError] = useState<string | null>(null)

  async function submit(event: FormEvent) {
    event.preventDefault()
    const start = new Date(startsAt)
    const end = new Date(start.getTime() + durationMinutes * 60_000)
    try {
      await api.post('/api/slots', {
        type,
        startsAt: toIsoDateTime(start),
        endsAt: toIsoDateTime(end),
        capacity,
      })
      setStartsAt('')
      setError(null)
      await onCreated()
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  return (
    <form className="card form" onSubmit={submit}>
      <h3>Nouveau créneau</h3>
      <ErrorBanner error={error} onClose={() => setError(null)} />
      <div className="row">
        <label className="field">
          Type
          <select value={type} onChange={(e) => setType(e.target.value as SlotType)}>
            {Object.entries(SLOT_LABELS).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </label>
        <label className="field">
          Début
          <input type="datetime-local" required value={startsAt} onChange={(e) => setStartsAt(e.target.value)} />
        </label>
        <label className="field">
          Durée (min)
          <input type="number" min={30} max={240} step={15} value={durationMinutes}
                 onChange={(e) => setDurationMinutes(Number(e.target.value))} />
        </label>
        <label className="field">
          Places
          <input type="number" min={1} max={200} value={capacity} onChange={(e) => setCapacity(Number(e.target.value))} />
        </label>
        <button type="submit" className="primary">
          Planifier
        </button>
      </div>
    </form>
  )
}
