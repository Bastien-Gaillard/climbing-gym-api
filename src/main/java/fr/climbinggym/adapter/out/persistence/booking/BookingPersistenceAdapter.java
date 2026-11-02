package fr.climbinggym.adapter.out.persistence.booking;

import fr.climbinggym.application.port.out.BookingRepository;
import fr.climbinggym.domain.booking.Booking;
import fr.climbinggym.domain.booking.BookingStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class BookingPersistenceAdapter implements BookingRepository {

    private final BookingJpaRepository jpa;

    BookingPersistenceAdapter(BookingJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Booking save(Booking b) {
        jpa.save(new BookingEntity(b.id(), b.slotId(), b.memberId(), b.subscriptionId(), b.bookedAt(), b.status()));
        return b;
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return jpa.findById(id).map(BookingPersistenceAdapter::toDomain);
    }

    @Override
    public List<Booking> findByMemberId(UUID memberId) {
        return jpa.findByMemberIdOrderByBookedAtDesc(memberId).stream().map(BookingPersistenceAdapter::toDomain).toList();
    }

    @Override
    public boolean existsConfirmed(UUID slotId, UUID memberId) {
        return jpa.existsBySlotIdAndMemberIdAndStatus(slotId, memberId, BookingStatus.CONFIRMED);
    }

    @Override
    public long countConfirmed(UUID slotId) {
        return jpa.countBySlotIdAndStatus(slotId, BookingStatus.CONFIRMED);
    }

    @Override
    public Map<UUID, Long> countConfirmedBySlot(Collection<UUID> slotIds) {
        if (slotIds.isEmpty()) return Map.of();
        return jpa.countBySlotIds(slotIds, BookingStatus.CONFIRMED).stream()
                .collect(Collectors.toMap(
                        BookingJpaRepository.SlotBookingCount::getSlotId,
                        BookingJpaRepository.SlotBookingCount::getTotal
                ));
    }

    private static Booking toDomain(BookingEntity e) {
        return Booking.restore(e.getId(), e.getSlotId(), e.getMemberId(), e.getSubscriptionId(), e.getBookedAt(), e.getStatus());
    }
}
