package fr.climbinggym.adapter.in.web.slot;

import fr.climbinggym.application.port.in.PlanSlotCommand;
import fr.climbinggym.domain.slot.SlotType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PlanSlotRequest(
        @NotNull SlotType type,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        @Min(1) @Max(200) int capacity
) {

    PlanSlotCommand toCommand() {
        return new PlanSlotCommand(type, startsAt, endsAt, capacity);
    }
}
