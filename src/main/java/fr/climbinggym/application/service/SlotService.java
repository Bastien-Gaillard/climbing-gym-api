package fr.climbinggym.application.service;

import fr.climbinggym.application.UseCase;
import fr.climbinggym.application.port.in.PlanSlotCommand;
import fr.climbinggym.application.port.in.SlotAvailability;
import fr.climbinggym.application.port.in.SlotUseCase;
import fr.climbinggym.application.port.out.BookingRepository;
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

    private static final int MAX_RANGE_DAYS = 62;

    private final SlotRepository slots;
    private final BookingRepository bookings;
    private final Clock clock;

    public SlotService(SlotRepository slots, BookingRepository bookings, Clock clock) {
        this.slots = slots;
        this.bookings = bookings;
        this.clock = clock;
    }

    @Override
    public Slot plan(PlanSlotCommand command) {
        var slot = Slot.plan(command.type(), command.startsAt(), command.endsAt(), command.capacity(), LocalDateTime.now(clock));
        return slots.save(slot);
    }

    @Override
    public SlotAvailability findById(UUID id) {
        var slot = slots.findById(id).orElseThrow(() -> new NotFoundException("Slot", id));
        return new SlotAvailability(slot, bookings.countConfirmed(id));
    }

    @Override
    public List<SlotAvailability> findBetween(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) throw new InvalidValueException("to", "must not be before from");
        if (from.plusDays(MAX_RANGE_DAYS).isBefore(to)) throw new InvalidValueException("to", "range is limited to " + MAX_RANGE_DAYS + " days");

        var found = slots.findStartingBetween(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        var counts = bookings.countConfirmedBySlot(found.stream().map(Slot::id).toList());

        return found.stream()
                .map(slot -> new SlotAvailability(slot, counts.getOrDefault(slot.id(), 0L)))
                .toList();
    }
}
