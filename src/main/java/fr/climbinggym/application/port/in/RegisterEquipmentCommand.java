package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.equipment.EquipmentCategory;

public record RegisterEquipmentCommand(String name, EquipmentCategory category, String size, int totalQuantity) {
}
