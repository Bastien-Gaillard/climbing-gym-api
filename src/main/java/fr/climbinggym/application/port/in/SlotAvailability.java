package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.slot.Slot;

public record SlotAvailability(Slot slot, long bookedCount) {

    public int remainingPlaces() {
        return slot.remainingPlaces(bookedCount);
    }
}
