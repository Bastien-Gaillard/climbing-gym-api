package fr.climbinggym.application.service;

import fr.climbinggym.application.UseCase;
import fr.climbinggym.application.port.in.EquipmentUseCase;
import fr.climbinggym.application.port.in.RegisterEquipmentCommand;
import fr.climbinggym.application.port.out.EquipmentRepository;
import fr.climbinggym.domain.equipment.Equipment;
import fr.climbinggym.domain.shared.NotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@UseCase
public class EquipmentService implements EquipmentUseCase {

    private final EquipmentRepository equipment;

    public EquipmentService(EquipmentRepository equipment) {
        this.equipment = equipment;
    }

    @Override
    public Equipment register(RegisterEquipmentCommand command) {
        return equipment.save(Equipment.register(command.name(), command.category(), command.size(), command.totalQuantity()));
    }

    @Override
    public List<Equipment> findAll() {
        return equipment.findAll();
    }

    @Override
    public Equipment rent(UUID id, int quantity) {
        return update(id, item -> item.rent(quantity));
    }

    @Override
    public Equipment giveBack(UUID id, int quantity) {
        return update(id, item -> item.giveBack(quantity));
    }

    @Override
    public Equipment restock(UUID id, int quantity) {
        return update(id, item -> item.restock(quantity));
    }

    private Equipment update(UUID id, Consumer<Equipment> change) {
        var item = equipment.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Equipment", id));
        change.accept(item);
        return equipment.save(item);
    }
}
