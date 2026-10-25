package fr.climbinggym.domain.equipment;

import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.shared.Guard;
import fr.climbinggym.domain.shared.InvalidValueException;

import java.util.UUID;

public final class Equipment {

    private final UUID id;
    private final String name;
    private final EquipmentCategory category;
    private final String size;
    private int totalQuantity;
    private int rentedQuantity;

    private Equipment(UUID id, String name, EquipmentCategory category, String size, int totalQuantity, int rentedQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.size = size;
        this.totalQuantity = totalQuantity;
        this.rentedQuantity = rentedQuantity;
    }

    public static Equipment register(String name, EquipmentCategory category, String size, int totalQuantity) {
        Guard.notNull(category, "category");
        if (totalQuantity < 0) throw new InvalidValueException("totalQuantity", "must not be negative");

        var normalizedSize = size == null || size.isBlank() ? null : size.strip();
        return new Equipment(UUID.randomUUID(), Guard.notBlank(name, "name"), category, normalizedSize, totalQuantity, 0);
    }

    public static Equipment restore(UUID id, String name, EquipmentCategory category, String size,
                                    int totalQuantity, int rentedQuantity) {
        return new Equipment(id, name, category, size, totalQuantity, rentedQuantity);
    }

    public int availableQuantity() {
        return totalQuantity - rentedQuantity;
    }

    public void rent(int quantity) {
        Guard.positive(quantity, "quantity");
        if (quantity > availableQuantity()) {
            throw new BusinessRuleException("EQUIPMENT_UNAVAILABLE",
                    "Only " + availableQuantity() + " " + name + " available");
        }
        rentedQuantity += quantity;
    }

    public void giveBack(int quantity) {
        Guard.positive(quantity, "quantity");
        if (quantity > rentedQuantity) {
            throw new BusinessRuleException("INVALID_RETURN", "Only " + rentedQuantity + " " + name + " are rented");
        }
        rentedQuantity -= quantity;
    }

    public void restock(int quantity) {
        Guard.positive(quantity, "quantity");
        totalQuantity += quantity;
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public EquipmentCategory category() {
        return category;
    }

    public String size() {
        return size;
    }

    public int totalQuantity() {
        return totalQuantity;
    }

    public int rentedQuantity() {
        return rentedQuantity;
    }
}
