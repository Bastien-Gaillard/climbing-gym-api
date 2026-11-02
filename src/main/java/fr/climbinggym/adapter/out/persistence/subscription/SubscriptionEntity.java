package fr.climbinggym.adapter.out.persistence.subscription;

import fr.climbinggym.domain.subscription.SubscriptionStatus;
import fr.climbinggym.domain.subscription.SubscriptionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity {

    @Id
    private UUID id;

    @Column(name = "member_id", nullable = false, updatable = false)
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private SubscriptionType type;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "remaining_entries")
    private Integer remainingEntries;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubscriptionStatus status;

    protected SubscriptionEntity() {
    }

    public SubscriptionEntity(UUID id, UUID memberId, SubscriptionType type, LocalDate startDate, LocalDate endDate,
                              Integer remainingEntries, SubscriptionStatus status) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingEntries = remainingEntries;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public SubscriptionType getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Integer getRemainingEntries() {
        return remainingEntries;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }
}
