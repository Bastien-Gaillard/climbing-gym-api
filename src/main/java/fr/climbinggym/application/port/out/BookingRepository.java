package fr.climbinggym.application.port.out;

import fr.climbinggym.domain.booking.Booking;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {

    Booking save(Booking booking);

    Optional<Booking> findById(UUID id);

    List<Booking> findByMemberId(UUID memberId);

    boolean existsConfirmed(UUID slotId, UUID memberId);

    long countConfirmed(UUID slotId);

    Map<UUID, Long> countConfirmedBySlot(Collection<UUID> slotIds);
}
