package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.booking.Booking;

import java.util.List;
import java.util.UUID;

public interface BookingUseCase {

    Booking book(UUID slotId, UUID memberId);

    Booking cancel(UUID bookingId);

    List<Booking> findByMember(UUID memberId);
}
