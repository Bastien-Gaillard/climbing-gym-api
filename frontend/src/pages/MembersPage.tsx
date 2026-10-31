import { useState, type FormEvent } from 'react'
import { api } from '../api/client'
import type { Booking, Member, Subscription, SubscriptionType } from '../api/types'
import { ErrorBanner } from '../components/ErrorBanner'
import { errorMessage, useApi } from '../hooks'
import { SUBSCRIPTION_LABELS } from '../labels'

export function MembersPage() {
  const [search, setSearch] = useState('')
  const [selected, setSelected] = useState<Member | null>(null)
  const members = useApi(() => api.get<Member[]>(`/api/members?search=${encodeURIComponent(search)}`), [search])

  return (
    <section className="split">
      <div>
        <input
          className="search"
          placeholder="Rechercher (nom, prénom, email)…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <ErrorBanner error={members.error} onClose={() => members.setError(null)} />
        <div className="card">
          <table className="table">
            <thead>
              <tr>
                <th>Nom</th>
                <th>Email</th>
                <th>Assurage</th>
              </tr>
            </thead>
            <tbody>
              {(members.data ?? []).map((m) => (
                <tr key={m.id} className={selected?.id === m.id ? 'selected' : ''} onClick={() => setSelected(m)}>
                  <td>
                    {m.lastName} {m.firstName}
                  </td>
                  <td className="muted">{m.email}</td>
                  <td>{m.belayCertified ? '✅' : '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {members.data?.length === 0 && <p className="muted">Aucun adhérent.</p>}
        </div>
        <NewMemberForm onCreated={members.refresh} />
      </div>

      <div>{selected ? <MemberDetail key={selected.id} member={selected} /> : <p className="muted">Sélectionne un adhérent.</p>}</div>
    </section>
  )
}

function MemberDetail({ member }: { member: Member }) {
  const [type, setType] = useState<SubscriptionType>('MONTHLY')
  const subscriptions = useApi(() => api.get<Subscription[]>(`/api/members/${member.id}/subscriptions`), [member.id])
  const bookings = useApi(() => api.get<Booking[]>(`/api/members/${member.id}/bookings`), [member.id])

  async function run(action: () => Promise<unknown>) {
    try {
      await action()
      await Promise.all([subscriptions.refresh(), bookings.refresh()])
    } catch (e) {
      subscriptions.setError(errorMessage(e))
    }
  }

  return (
    <div className="card">
      <h3>
        {member.firstName} {member.lastName}
      </h3>
      <p className="muted">
        Né(e) le {new Date(member.birthDate).toLocaleDateString('fr-FR')} · {member.email}
      </p>
      <ErrorBanner error={subscriptions.error} onClose={() => subscriptions.setError(null)} />

      <h4>Abonnements</h4>
      <ul className="list">
        {(subscriptions.data ?? []).map((s) => (
          <li key={s.id}>
            <span>
              {SUBSCRIPTION_LABELS[s.type]} · {new Date(s.startDate).toLocaleDateString('fr-FR')} →{' '}
              {new Date(s.endDate).toLocaleDateString('fr-FR')}
              {s.remainingEntries !== null && ` · ${s.remainingEntries} entrée(s)`}
            </span>
            {s.status === 'ACTIVE' ? (
              <button className="link" onClick={() => run(() => api.post(`/api/subscriptions/${s.id}/cancel`))}>
                Résilier
              </button>
            ) : (
              <span className="muted">résilié</span>
            )}
          </li>
        ))}
      </ul>
      <div className="row">
        <select value={type} onChange={(e) => setType(e.target.value as SubscriptionType)}>
          {Object.entries(SUBSCRIPTION_LABELS).map(([value, label]) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
        <button onClick={() => run(() => api.post(`/api/members/${member.id}/subscriptions`, { type }))}>Souscrire</button>
      </div>

      <h4>Réservations</h4>
      <ul className="list">
        {(bookings.data ?? []).map((b) => (
          <li key={b.id}>
            <span>
              Réservé le {new Date(b.bookedAt).toLocaleString('fr-FR')} · {b.status === 'CONFIRMED' ? 'confirmée' : 'annulée'}
            </span>
            {b.status === 'CONFIRMED' && (
              <button className="link" onClick={() => run(() => api.post(`/api/bookings/${b.id}/cancel`))}>
                Annuler
              </button>
            )}
          </li>
        ))}
        {bookings.data?.length === 0 && <li className="muted">Aucune réservation.</li>}
      </ul>
    </div>
  )
}

function NewMemberForm({ onCreated }: { onCreated: () => Promise<void> }) {
  const empty = { firstName: '', lastName: '', email: '', birthDate: '', belayCertified: false }
  const [form, setForm] = useState(empty)
  const [error, setError] = useState<string | null>(null)

  async function submit(event: FormEvent) {
    event.preventDefault()
    try {
      await api.post('/api/members', form)
      setForm(empty)
      setError(null)
      await onCreated()
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  return (
    <form className="card form" onSubmit={submit}>
      <h3>Nouvel adhérent</h3>
      <ErrorBanner error={error} onClose={() => setError(null)} />
      <div className="row">
        <input placeholder="Prénom" required value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} />
        <input placeholder="Nom" required value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} />
        <input type="email" placeholder="Email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input type="date" required value={form.birthDate} onChange={(e) => setForm({ ...form, birthDate: e.target.value })} />
        <label className="field inline">
          <input type="checkbox" checked={form.belayCertified} onChange={(e) => setForm({ ...form, belayCertified: e.target.checked })} />
          Passeport assurage
        </label>
        <button type="submit" className="primary">
          Inscrire
        </button>
      </div>
    </form>
  )
}
