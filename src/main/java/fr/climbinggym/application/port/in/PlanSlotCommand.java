package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.slot.SlotType;

import java.time.LocalDateTime;

public record PlanSlotCommand(SlotType type, LocalDateTime startsAt, LocalDateTime endsAt, int capacity) {
}
