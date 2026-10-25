package fr.climbinggym.support.fake;

import fr.climbinggym.application.port.out.SlotRepository;
import fr.climbinggym.domain.slot.Slot;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemorySlotRepository implements SlotRepository {

    private final Map<UUID, Slot> store = new LinkedHashMap<>();

    @Override
    public Slot save(Slot slot) {
        store.put(slot.id(), slot);
        return slot;
    }

    @Override
    public Optional<Slot> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Slot> findByIdForUpdate(UUID id) {
        return findById(id);
    }

    @Override
    public List<Slot> findStartingBetween(LocalDateTime from, LocalDateTime to) {
        return store.values().stream()
                .filter(slot -> !slot.startsAt().isBefore(from) && slot.startsAt().isBefore(to))
                .sorted(Comparator.comparing(Slot::startsAt))
                .toList();
    }
}
