package fr.climbinggym.adapter.out.persistence.slot;

import fr.climbinggym.application.port.out.SlotRepository;
import fr.climbinggym.domain.slot.Slot;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class SlotPersistenceAdapter implements SlotRepository {

    private final SlotJpaRepository jpa;

    SlotPersistenceAdapter(SlotJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Slot save(Slot slot) {
        jpa.save(new SlotEntity(slot.id(), slot.type(), slot.startsAt(), slot.endsAt(), slot.capacity()));
        return slot;
    }

    @Override
    public Optional<Slot> findById(UUID id) {
        return jpa.findById(id).map(SlotPersistenceAdapter::toDomain);
    }

    @Override
    public Optional<Slot> findByIdForUpdate(UUID id) {
        return jpa.findByIdForUpdate(id).map(SlotPersistenceAdapter::toDomain);
    }

    @Override
    public List<Slot> findStartingBetween(LocalDateTime from, LocalDateTime to) {
        return jpa.findByStartsAtGreaterThanEqualAndStartsAtLessThanOrderByStartsAtAsc(from, to).stream()
                .map(SlotPersistenceAdapter::toDomain)
                .toList();
    }

    private static Slot toDomain(SlotEntity e) {
        return Slot.restore(e.getId(), e.getType(), e.getStartsAt(), e.getEndsAt(), e.getCapacity());
    }
}
