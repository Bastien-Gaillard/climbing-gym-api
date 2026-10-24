package fr.climbinggym.adapter.in.web.slot;

import fr.climbinggym.adapter.in.web.ApiResponse;
import fr.climbinggym.application.port.in.SlotUseCase;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotUseCase slots;

    public SlotController(SlotUseCase slots) {
        this.slots = slots;
    }

    @GetMapping
    public ApiResponse<List<SlotResponse>> findBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.of(slots.findBetween(from, to).stream().map(SlotResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<SlotResponse> findById(@PathVariable UUID id) {
        return ApiResponse.of(SlotResponse.from(slots.findById(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SlotResponse> plan(@Valid @RequestBody PlanSlotRequest request) {
        return ApiResponse.of(SlotResponse.from(slots.plan(request.toCommand())));
    }
}
