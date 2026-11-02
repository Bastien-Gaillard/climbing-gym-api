package fr.climbinggym.adapter.out.persistence.slot;

import fr.climbinggym.domain.slot.SlotType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "slots")
public class SlotEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private SlotType type;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    protected SlotEntity() {
    }

    public SlotEntity(UUID id, SlotType type, LocalDateTime startsAt, LocalDateTime endsAt, int capacity) {
        this.id = id;
        this.type = type;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.capacity = capacity;
    }

    public UUID getId() {
        return id;
    }

    public SlotType getType() {
        return type;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public int getCapacity() {
        return capacity;
    }
}
