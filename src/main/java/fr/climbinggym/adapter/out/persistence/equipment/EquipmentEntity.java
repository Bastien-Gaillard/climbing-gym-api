package fr.climbinggym.adapter.out.persistence.equipment;

import fr.climbinggym.domain.equipment.EquipmentCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "equipment_items")
public class EquipmentEntity {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private EquipmentCategory category;

    @Column(name = "size", length = 20)
    private String size;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "rented_quantity", nullable = false)
    private int rentedQuantity;

    protected EquipmentEntity() {
    }

    public EquipmentEntity(UUID id, String name, EquipmentCategory category, String size, int totalQuantity, int rentedQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.size = size;
        this.totalQuantity = totalQuantity;
        this.rentedQuantity = rentedQuantity;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EquipmentCategory getCategory() {
        return category;
    }

    public String getSize() {
        return size;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getRentedQuantity() {
        return rentedQuantity;
    }
}
