package fr.climbinggym.support.fake;

import fr.climbinggym.application.port.out.BookingRepository;
import fr.climbinggym.domain.booking.Booking;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {

    private final Map<UUID, Booking> store = new LinkedHashMap<>();

    @Override
    public Booking save(Booking booking) {
        store.put(booking.id(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Booking> findByMemberId(UUID memberId) {
        return store.values().stream().filter(b -> b.memberId().equals(memberId)).toList();
    }

    @Override
    public boolean existsConfirmed(UUID slotId, UUID memberId) {
        return store.values().stream()
                .anyMatch(b -> b.isConfirmed() && b.slotId().equals(slotId) && b.memberId().equals(memberId));
    }

    @Override
    public long countConfirmed(UUID slotId) {
        return store.values().stream().filter(b -> b.isConfirmed() && b.slotId().equals(slotId)).count();
    }

    @Override
    public Map<UUID, Long> countConfirmedBySlot(Collection<UUID> slotIds) {
        return store.values().stream()
                .filter(b -> b.isConfirmed() && slotIds.contains(b.slotId()))
                .collect(Collectors.groupingBy(Booking::slotId, Collectors.counting()));
    }
}
