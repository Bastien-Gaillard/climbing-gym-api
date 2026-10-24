package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.slot.Slot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SlotUseCase {

    Slot plan(PlanSlotCommand command);

    Slot findById(UUID id);

    List<Slot> findBetween(LocalDate from, LocalDate to);
}
