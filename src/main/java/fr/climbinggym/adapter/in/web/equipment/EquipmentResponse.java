package fr.climbinggym.adapter.in.web.equipment;

import fr.climbinggym.domain.equipment.Equipment;
import fr.climbinggym.domain.equipment.EquipmentCategory;

import java.util.UUID;

public record EquipmentResponse(
        UUID id,
        String name,
        EquipmentCategory category,
        String size,
        int totalQuantity,
        int rentedQuantity,
        int availableQuantity
) {

    public static EquipmentResponse from(Equipment e) {
        return new EquipmentResponse(e.id(), e.name(), e.category(), e.size(), e.totalQuantity(), e.rentedQuantity(), e.availableQuantity());
    }
}
