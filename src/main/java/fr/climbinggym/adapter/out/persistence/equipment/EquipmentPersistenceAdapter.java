package fr.climbinggym.adapter.out.persistence.equipment;

import fr.climbinggym.application.port.out.EquipmentRepository;
import fr.climbinggym.domain.equipment.Equipment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class EquipmentPersistenceAdapter implements EquipmentRepository {

    private final EquipmentJpaRepository jpa;

    EquipmentPersistenceAdapter(EquipmentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Equipment save(Equipment e) {
        jpa.save(new EquipmentEntity(e.id(), e.name(), e.category(), e.size(), e.totalQuantity(), e.rentedQuantity()));
        return e;
    }

    @Override
    public Optional<Equipment> findByIdForUpdate(UUID id) {
        return jpa.findByIdForUpdate(id).map(EquipmentPersistenceAdapter::toDomain);
    }

    @Override
    public List<Equipment> findAll() {
        return jpa.findAllByOrderByCategoryAscNameAscSizeAsc().stream().map(EquipmentPersistenceAdapter::toDomain).toList();
    }

    private static Equipment toDomain(EquipmentEntity e) {
        return Equipment.restore(e.getId(), e.getName(), e.getCategory(), e.getSize(), e.getTotalQuantity(), e.getRentedQuantity());
    }
}
