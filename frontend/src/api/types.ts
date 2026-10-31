export type SlotType = 'BOULDER' | 'ROPE' | 'COURSE' | 'KIDS'
export type SubscriptionType = 'MONTHLY' | 'ANNUAL' | 'TEN_ENTRIES'
export type EquipmentCategory = 'SHOES' | 'HARNESS' | 'BELAY_DEVICE' | 'HELMET' | 'ROPE' | 'CHALK_BAG'

export interface Member {
  id: string
  firstName: string
  lastName: string
  email: string
  birthDate: string
  belayCertified: boolean
  createdAt: string
}

export interface Subscription {
  id: string
  memberId: string
  type: SubscriptionType
  startDate: string
  endDate: string
  remainingEntries: number | null
  status: 'ACTIVE' | 'CANCELLED'
}

export interface Slot {
  id: string
  type: SlotType
  startsAt: string
  endsAt: string
  capacity: number
  bookedCount: number
  remainingPlaces: number
}

export interface Booking {
  id: string
  slotId: string
  memberId: string
  subscriptionId: string
  bookedAt: string
  status: 'CONFIRMED' | 'CANCELLED'
}

export interface Equipment {
  id: string
  name: string
  category: EquipmentCategory
  size: string | null
  totalQuantity: number
  rentedQuantity: number
  availableQuantity: number
}
