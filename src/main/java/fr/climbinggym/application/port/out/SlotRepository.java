package fr.climbinggym.application.port.out;

import fr.climbinggym.domain.slot.Slot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SlotRepository {

    Slot save(Slot slot);

    Optional<Slot> findById(UUID id);

    /**
     * Loads the slot and locks it until the end of the transaction,
     * so concurrent bookings on the same slot are serialized.
     */
    Optional<Slot> findByIdForUpdate(UUID id);

    List<Slot> findStartingBetween(LocalDateTime from, LocalDateTime to);
}
