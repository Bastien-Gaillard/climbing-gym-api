package fr.climbinggym.adapter.in.web.slot;

import fr.climbinggym.application.port.in.SlotAvailability;
import fr.climbinggym.domain.slot.Slot;
import fr.climbinggym.domain.slot.SlotType;

import java.time.LocalDateTime;
import java.util.UUID;

public record SlotResponse(
        UUID id,
        SlotType type,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        int capacity,
        long bookedCount,
        int remainingPlaces
) {

    public static SlotResponse from(SlotAvailability availability) {
        var slot = availability.slot();
        return new SlotResponse(slot.id(), slot.type(), slot.startsAt(), slot.endsAt(), slot.capacity(),
                availability.bookedCount(), availability.remainingPlaces());
    }

    public static SlotResponse from(Slot slot) {
        return from(new SlotAvailability(slot, 0));
    }
}
