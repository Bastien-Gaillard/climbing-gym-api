package fr.climbinggym.adapter.in.web.equipment;

import fr.climbinggym.application.port.in.RegisterEquipmentCommand;
import fr.climbinggym.domain.equipment.EquipmentCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterEquipmentRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull EquipmentCategory category,
        @Size(max = 20) String size,
        @Min(0) int totalQuantity
) {

    RegisterEquipmentCommand toCommand() {
        return new RegisterEquipmentCommand(name, category, size, totalQuantity);
    }
}
