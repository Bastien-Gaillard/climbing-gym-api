import { useState, type FormEvent } from 'react'
import { api } from '../api/client'
import type { Equipment, EquipmentCategory } from '../api/types'
import { ErrorBanner } from '../components/ErrorBanner'
import { errorMessage, useApi } from '../hooks'
import { EQUIPMENT_LABELS } from '../labels'

type Movement = 'rentals' | 'returns' | 'restock'

export function EquipmentPage() {
  const equipment = useApi(() => api.get<Equipment[]>('/api/equipment'), [])

  async function move(item: Equipment, movement: Movement) {
    try {
      await api.post(`/api/equipment/${item.id}/${movement}`, { quantity: 1 })
      await equipment.refresh()
    } catch (e) {
      equipment.setError(errorMessage(e))
    }
  }

  return (
    <section>
      <ErrorBanner error={equipment.error} onClose={() => equipment.setError(null)} />
      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Matériel</th>
              <th>Catégorie</th>
              <th>Taille</th>
              <th>Disponible</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {(equipment.data ?? []).map((item) => (
              <tr key={item.id}>
                <td>{item.name}</td>
                <td className="muted">{EQUIPMENT_LABELS[item.category]}</td>
                <td>{item.size ?? '—'}</td>
                <td>
                  <StockBar item={item} />
                </td>
                <td className="actions">
                  <button disabled={item.availableQuantity === 0} onClick={() => move(item, 'rentals')}>
                    Louer
                  </button>
                  <button disabled={item.rentedQuantity === 0} onClick={() => move(item, 'returns')}>
                    Retour
                  </button>
                  <button onClick={() => move(item, 'restock')}>+1</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {equipment.data?.length === 0 && <p className="muted">Aucun matériel enregistré.</p>}
      </div>
      <NewEquipmentForm onCreated={equipment.refresh} />
    </section>
  )
}

function StockBar({ item }: { item: Equipment }) {
  const ratio = item.totalQuantity === 0 ? 0 : item.availableQuantity / item.totalQuantity
  return (
    <div className="stock">
      <div className="stock-track">
        <div className={ratio < 0.2 ? 'stock-fill low' : 'stock-fill'} style={{ width: `${ratio * 100}%` }} />
      </div>
      <span>
        {item.availableQuantity} / {item.totalQuantity}
      </span>
    </div>
  )
}

function NewEquipmentForm({ onCreated }: { onCreated: () => Promise<void> }) {
  const empty = { name: '', category: 'SHOES' as EquipmentCategory, size: '', totalQuantity: 1 }
  const [form, setForm] = useState(empty)
  const [error, setError] = useState<string | null>(null)

  async function submit(event: FormEvent) {
    event.preventDefault()
    try {
      await api.post('/api/equipment', form)
      setForm(empty)
      setError(null)
      await onCreated()
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  return (
    <form className="card form" onSubmit={submit}>
      <h3>Ajouter du matériel</h3>
      <ErrorBanner error={error} onClose={() => setError(null)} />
      <div className="row">
        <input placeholder="Nom" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value as EquipmentCategory })}>
          {Object.entries(EQUIPMENT_LABELS).map(([value, label]) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
        <input placeholder="Taille" value={form.size} onChange={(e) => setForm({ ...form, size: e.target.value })} />
        <input type="number" min={0} value={form.totalQuantity}
               onChange={(e) => setForm({ ...form, totalQuantity: Number(e.target.value) })} />
        <button type="submit" className="primary">
          Ajouter
        </button>
      </div>
    </form>
  )
}
