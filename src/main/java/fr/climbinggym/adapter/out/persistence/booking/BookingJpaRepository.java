package fr.climbinggym.adapter.out.persistence.booking;

import fr.climbinggym.domain.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

interface BookingJpaRepository extends JpaRepository<BookingEntity, UUID> {

    List<BookingEntity> findByMemberIdOrderByBookedAtDesc(UUID memberId);

    boolean existsBySlotIdAndMemberIdAndStatus(UUID slotId, UUID memberId, BookingStatus status);

    long countBySlotIdAndStatus(UUID slotId, BookingStatus status);

    @Query("""
            select b.slotId as slotId, count(b) as total
            from BookingEntity b
            where b.status = :status and b.slotId in :slotIds
            group by b.slotId
            """)
    List<SlotBookingCount> countBySlotIds(@Param("slotIds") Collection<UUID> slotIds, @Param("status") BookingStatus status);

    interface SlotBookingCount {
        UUID getSlotId();

        Long getTotal();
    }
}
