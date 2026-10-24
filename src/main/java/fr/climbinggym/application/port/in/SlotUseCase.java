package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.slot.Slot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SlotUseCase {

    Slot plan(PlanSlotCommand command);

    SlotAvailability findById(UUID id);

    List<SlotAvailability> findBetween(LocalDate from, LocalDate to);
}
