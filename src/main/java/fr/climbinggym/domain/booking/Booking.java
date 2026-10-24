package fr.climbinggym.domain.booking;

import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.slot.Slot;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Booking {

    private final UUID id;
    private final UUID slotId;
    private final UUID memberId;
    private final UUID subscriptionId;
    private final LocalDateTime bookedAt;
    private BookingStatus status;

    private Booking(UUID id, UUID slotId, UUID memberId, UUID subscriptionId, LocalDateTime bookedAt, BookingStatus status) {
        this.id = id;
        this.slotId = slotId;
        this.memberId = memberId;
        this.subscriptionId = subscriptionId;
        this.bookedAt = bookedAt;
        this.status = status;
    }

    public static Booking confirm(UUID slotId, UUID memberId, UUID subscriptionId, LocalDateTime now) {
        return new Booking(UUID.randomUUID(), slotId, memberId, subscriptionId, now, BookingStatus.CONFIRMED);
    }

    public static Booking restore(UUID id, UUID slotId, UUID memberId, UUID subscriptionId,
                                  LocalDateTime bookedAt, BookingStatus status) {
        return new Booking(id, slotId, memberId, subscriptionId, bookedAt, status);
    }

    public void cancel(Slot slot, LocalDateTime now) {
        if (status == BookingStatus.CANCELLED) {
            throw new BusinessRuleException("BOOKING_ALREADY_CANCELLED", "Booking " + id + " is already cancelled");
        }
        if (slot.hasStartedAt(now)) {
            throw new BusinessRuleException("SLOT_ALREADY_STARTED", "Cannot cancel a booking once the slot has started");
        }
        status = BookingStatus.CANCELLED;
    }

    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    public UUID id() {
        return id;
    }

    public UUID slotId() {
        return slotId;
    }

    public UUID memberId() {
        return memberId;
    }

    public UUID subscriptionId() {
        return subscriptionId;
    }

    public LocalDateTime bookedAt() {
        return bookedAt;
    }

    public BookingStatus status() {
        return status;
    }
}
