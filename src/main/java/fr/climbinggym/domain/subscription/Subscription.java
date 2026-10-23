package fr.climbinggym.domain.subscription;

import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.shared.Guard;

import java.time.LocalDate;
import java.util.UUID;

public final class Subscription {

    private final UUID id;
    private final UUID memberId;
    private final SubscriptionType type;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private Integer remainingEntries;
    private SubscriptionStatus status;

    private Subscription(UUID id, UUID memberId, SubscriptionType type, LocalDate startDate, LocalDate endDate,
                         Integer remainingEntries, SubscriptionStatus status) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingEntries = remainingEntries;
        this.status = status;
    }

    public static Subscription subscribe(UUID memberId, SubscriptionType type, LocalDate startDate) {
        Guard.notNull(memberId, "memberId");
        Guard.notNull(type, "type");
        Guard.notNull(startDate, "startDate");

        var endDate = startDate.plusMonths(type.validityMonths()).minusDays(1);
        return new Subscription(UUID.randomUUID(), memberId, type, startDate, endDate, type.entries(), SubscriptionStatus.ACTIVE);
    }

    public static Subscription restore(UUID id, UUID memberId, SubscriptionType type, LocalDate startDate,
                                       LocalDate endDate, Integer remainingEntries, SubscriptionStatus status) {
        return new Subscription(id, memberId, type, startDate, endDate, remainingEntries, status);
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    public boolean covers(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public boolean isValidOn(LocalDate date) {
        if (!isActive() || !covers(date)) return false;
        return !type.isEntryBased() || remainingEntries > 0;
    }

    public boolean overlaps(Subscription other) {
        return !other.endDate.isBefore(startDate) && !other.startDate.isAfter(endDate);
    }

    public void consumeEntry() {
        if (!type.isEntryBased()) return;
        if (remainingEntries <= 0) throw new BusinessRuleException("NO_ENTRY_LEFT", "Subscription " + id + " has no entry left");
        remainingEntries--;
    }

    public void refundEntry() {
        if (!type.isEntryBased()) return;
        remainingEntries = Math.min(remainingEntries + 1, type.entries());
    }

    public void cancel() {
        if (!isActive()) throw new BusinessRuleException("SUBSCRIPTION_ALREADY_CANCELLED", "Subscription " + id + " is already cancelled");
        status = SubscriptionStatus.CANCELLED;
    }

    public UUID id() {
        return id;
    }

    public UUID memberId() {
        return memberId;
    }

    public SubscriptionType type() {
        return type;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public Integer remainingEntries() {
        return remainingEntries;
    }

    public SubscriptionStatus status() {
        return status;
    }
}
