import { useState } from 'react'
import { EquipmentPage } from './pages/EquipmentPage'
import { MembersPage } from './pages/MembersPage'
import { PlanningPage } from './pages/PlanningPage'

const TABS = ['Planning', 'Adhérents', 'Matériel'] as const
type Tab = (typeof TABS)[number]

function App() {
  const [tab, setTab] = useState<Tab>('Planning')

  return (
    <div className="layout">
      <header className="header">
        <h1>🧗 Salle de grimpe</h1>
        <nav>
          {TABS.map((name) => (
            <button key={name} className={name === tab ? 'tab active' : 'tab'} onClick={() => setTab(name)}>
              {name}
            </button>
          ))}
        </nav>
      </header>
      <main>
        {tab === 'Planning' && <PlanningPage />}
        {tab === 'Adhérents' && <MembersPage />}
        {tab === 'Matériel' && <EquipmentPage />}
      </main>
    </div>
  )
}

export default App
