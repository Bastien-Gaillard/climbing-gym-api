package fr.climbinggym.adapter.in.web.slot;

import fr.climbinggym.domain.slot.Slot;
import fr.climbinggym.domain.slot.SlotType;

import java.time.LocalDateTime;
import java.util.UUID;

public record SlotResponse(UUID id, SlotType type, LocalDateTime startsAt, LocalDateTime endsAt, int capacity) {

    public static SlotResponse from(Slot slot) {
        return new SlotResponse(slot.id(), slot.type(), slot.startsAt(), slot.endsAt(), slot.capacity());
    }
}
