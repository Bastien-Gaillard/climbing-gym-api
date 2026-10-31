import type { EquipmentCategory, SlotType, SubscriptionType } from './api/types'

export const SLOT_LABELS: Record<SlotType, string> = {
  BOULDER: 'Bloc',
  ROPE: 'Voie (corde)',
  COURSE: 'Cours',
  KIDS: 'Enfants',
}

export const SUBSCRIPTION_LABELS: Record<SubscriptionType, string> = {
  MONTHLY: 'Mensuel',
  ANNUAL: 'Annuel',
  TEN_ENTRIES: 'Carte 10 entrées',
}

export const EQUIPMENT_LABELS: Record<EquipmentCategory, string> = {
  SHOES: 'Chaussons',
  HARNESS: 'Baudrier',
  BELAY_DEVICE: 'Système d’assurage',
  HELMET: 'Casque',
  ROPE: 'Corde',
  CHALK_BAG: 'Sac à magnésie',
}
