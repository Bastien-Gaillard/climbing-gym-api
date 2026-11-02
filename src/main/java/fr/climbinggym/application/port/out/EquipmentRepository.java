package fr.climbinggym.application.port.out;

import fr.climbinggym.domain.equipment.Equipment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository {

    Equipment save(Equipment equipment);

    Optional<Equipment> findByIdForUpdate(UUID id);

    List<Equipment> findAll();
}
