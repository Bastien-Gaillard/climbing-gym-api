package fr.climbinggym.domain.slot;

import fr.climbinggym.domain.member.Member;
import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.shared.Guard;
import fr.climbinggym.domain.shared.InvalidValueException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public final class Slot {

    private static final Duration MAX_DURATION = Duration.ofHours(4);

    private final UUID id;
    private final SlotType type;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final int capacity;

    private Slot(UUID id, SlotType type, LocalDateTime startsAt, LocalDateTime endsAt, int capacity) {
        this.id = id;
        this.type = type;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.capacity = capacity;
    }

    public static Slot plan(SlotType type, LocalDateTime startsAt, LocalDateTime endsAt, int capacity, LocalDateTime now) {
        Guard.notNull(type, "type");
        Guard.notNull(startsAt, "startsAt");
        Guard.notNull(endsAt, "endsAt");
        Guard.positive(capacity, "capacity");

        if (!startsAt.isAfter(now)) throw new InvalidValueException("startsAt", "must be in the future");
        if (!endsAt.isAfter(startsAt)) throw new InvalidValueException("endsAt", "must be after startsAt");
        if (Duration.between(startsAt, endsAt).compareTo(MAX_DURATION) > 0) {
            throw new InvalidValueException("endsAt", "slot cannot last more than " + MAX_DURATION.toHours() + " hours");
        }

        return new Slot(UUID.randomUUID(), type, startsAt, endsAt, capacity);
    }

    public static Slot restore(UUID id, SlotType type, LocalDateTime startsAt, LocalDateTime endsAt, int capacity) {
        return new Slot(id, type, startsAt, endsAt, capacity);
    }

    public boolean hasStartedAt(LocalDateTime now) {
        return !now.isBefore(startsAt);
    }

    public void checkBookableBy(Member member, long confirmedBookings, LocalDateTime now) {
        if (hasStartedAt(now)) {
            throw new BusinessRuleException("SLOT_ALREADY_STARTED", "Slot " + id + " has already started");
        }
        if (confirmedBookings >= capacity) {
            throw new BusinessRuleException("SLOT_FULL", "Slot " + id + " is full");
        }
        if (type.requiresBelayCertification() && !member.belayCertified()) {
            throw new BusinessRuleException("BELAY_CERTIFICATION_REQUIRED", "Rope slots require a belay certification");
        }
        if (type.isKidsOnly() && !member.isMinorOn(startsAt.toLocalDate())) {
            throw new BusinessRuleException("KIDS_ONLY_SLOT", "This slot is reserved to members under 18");
        }
    }

    public int remainingPlaces(long confirmedBookings) {
        return (int) Math.max(capacity - confirmedBookings, 0);
    }

    public UUID id() {
        return id;
    }

    public SlotType type() {
        return type;
    }

    public LocalDateTime startsAt() {
        return startsAt;
    }

    public LocalDateTime endsAt() {
        return endsAt;
    }

    public int capacity() {
        return capacity;
    }
}
