package fr.climbinggym.application.service;

import fr.climbinggym.application.UseCase;
import fr.climbinggym.application.port.in.PlanSlotCommand;
import fr.climbinggym.application.port.in.SlotUseCase;
import fr.climbinggym.application.port.out.SlotRepository;
import fr.climbinggym.domain.shared.InvalidValueException;
import fr.climbinggym.domain.shared.NotFoundException;
import fr.climbinggym.domain.slot.Slot;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@UseCase
public class SlotService implements SlotUseCase {

    private final SlotRepository slots;
    private final Clock clock;

    public SlotService(SlotRepository slots, Clock clock) {
        this.slots = slots;
        this.clock = clock;
    }

    @Override
    public Slot plan(PlanSlotCommand command) {
        var slot = Slot.plan(command.type(), command.startsAt(), command.endsAt(), command.capacity(), LocalDateTime.now(clock));
        return slots.save(slot);
    }

    @Override
    public Slot findById(UUID id) {
        return slots.findById(id).orElseThrow(() -> new NotFoundException("Slot", id));
    }

    @Override
    public List<Slot> findBetween(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) throw new InvalidValueException("to", "must not be before from");
        return slots.findStartingBetween(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }
}
