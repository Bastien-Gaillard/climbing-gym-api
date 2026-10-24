package fr.climbinggym.adapter.in.web.booking;

import fr.climbinggym.adapter.in.web.ApiResponse;
import fr.climbinggym.application.port.in.BookingUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class BookingController {

    private final BookingUseCase bookings;

    public BookingController(BookingUseCase bookings) {
        this.bookings = bookings;
    }

    @PostMapping("/api/slots/{slotId}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookingResponse> book(@PathVariable UUID slotId, @Valid @RequestBody BookSlotRequest request) {
        return ApiResponse.of(BookingResponse.from(bookings.book(slotId, request.memberId())));
    }

    @PostMapping("/api/bookings/{id}/cancel")
    public ApiResponse<BookingResponse> cancel(@PathVariable UUID id) {
        return ApiResponse.of(BookingResponse.from(bookings.cancel(id)));
    }

    @GetMapping("/api/members/{memberId}/bookings")
    public ApiResponse<List<BookingResponse>> findByMember(@PathVariable UUID memberId) {
        return ApiResponse.of(bookings.findByMember(memberId).stream().map(BookingResponse::from).toList());
    }
}
