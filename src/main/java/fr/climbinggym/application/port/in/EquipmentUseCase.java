package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.equipment.Equipment;

import java.util.List;
import java.util.UUID;

public interface EquipmentUseCase {

    Equipment register(RegisterEquipmentCommand command);

    List<Equipment> findAll();

    Equipment rent(UUID id, int quantity);

    Equipment giveBack(UUID id, int quantity);

    Equipment restock(UUID id, int quantity);
}
