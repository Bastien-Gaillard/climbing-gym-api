package fr.climbinggym.adapter.in.web.booking;

import fr.climbinggym.domain.booking.Booking;
import fr.climbinggym.domain.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID slotId,
        UUID memberId,
        UUID subscriptionId,
        LocalDateTime bookedAt,
        BookingStatus status
) {

    public static BookingResponse from(Booking b) {
        return new BookingResponse(b.id(), b.slotId(), b.memberId(), b.subscriptionId(), b.bookedAt(), b.status());
    }
}
