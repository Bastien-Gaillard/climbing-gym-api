package fr.climbinggym.adapter.in.web.equipment;

import fr.climbinggym.adapter.in.web.ApiResponse;
import fr.climbinggym.application.port.in.EquipmentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentUseCase equipment;

    public EquipmentController(EquipmentUseCase equipment) {
        this.equipment = equipment;
    }

    @GetMapping
    public ApiResponse<List<EquipmentResponse>> findAll() {
        return ApiResponse.of(equipment.findAll().stream().map(EquipmentResponse::from).toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EquipmentResponse> register(@Valid @RequestBody RegisterEquipmentRequest request) {
        return ApiResponse.of(EquipmentResponse.from(equipment.register(request.toCommand())));
    }

    @PostMapping("/{id}/rentals")
    public ApiResponse<EquipmentResponse> rent(@PathVariable UUID id, @Valid @RequestBody QuantityRequest request) {
        return ApiResponse.of(EquipmentResponse.from(equipment.rent(id, request.quantity())));
    }

    @PostMapping("/{id}/returns")
    public ApiResponse<EquipmentResponse> giveBack(@PathVariable UUID id, @Valid @RequestBody QuantityRequest request) {
        return ApiResponse.of(EquipmentResponse.from(equipment.giveBack(id, request.quantity())));
    }

    @PostMapping("/{id}/restock")
    public ApiResponse<EquipmentResponse> restock(@PathVariable UUID id, @Valid @RequestBody QuantityRequest request) {
        return ApiResponse.of(EquipmentResponse.from(equipment.restock(id, request.quantity())));
    }
}
