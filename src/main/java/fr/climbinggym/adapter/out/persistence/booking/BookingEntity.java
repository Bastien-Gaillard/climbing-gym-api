package fr.climbinggym.adapter.out.persistence.booking;

import fr.climbinggym.domain.booking.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    private UUID id;

    @Column(name = "slot_id", nullable = false, updatable = false)
    private UUID slotId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private UUID memberId;

    @Column(name = "subscription_id", nullable = false, updatable = false)
    private UUID subscriptionId;

    @Column(name = "booked_at", nullable = false, updatable = false)
    private LocalDateTime bookedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status;

    protected BookingEntity() {
    }

    public BookingEntity(UUID id, UUID slotId, UUID memberId, UUID subscriptionId, LocalDateTime bookedAt, BookingStatus status) {
        this.id = id;
        this.slotId = slotId;
        this.memberId = memberId;
        this.subscriptionId = subscriptionId;
        this.bookedAt = bookedAt;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSlotId() {
        return slotId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public UUID getSubscriptionId() {
        return subscriptionId;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public BookingStatus getStatus() {
        return status;
    }
}
