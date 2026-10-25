package fr.climbinggym.domain.equipment;

import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.shared.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquipmentTest {

    @Test
    void rent_and_give_back_update_availability() {
        var shoes = Equipment.register("Chaussons", EquipmentCategory.SHOES, "42", 6);

        shoes.rent(4);
        assertThat(shoes.availableQuantity()).isEqualTo(2);

        shoes.giveBack(3);
        assertThat(shoes.availableQuantity()).isEqualTo(5);
        assertThat(shoes.rentedQuantity()).isEqualTo(1);
    }

    @Test
    void cannot_rent_more_than_available() {
        var harness = Equipment.register("Baudrier", EquipmentCategory.HARNESS, "M", 2);

        assertThatThrownBy(() -> harness.rent(3))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Only 2");
    }

    @Test
    void cannot_give_back_more_than_rented() {
        var harness = Equipment.register("Baudrier", EquipmentCategory.HARNESS, "M", 2);
        harness.rent(1);

        assertThatThrownBy(() -> harness.giveBack(2)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void quantities_must_be_positive() {
        var rope = Equipment.register("Corde 70m", EquipmentCategory.ROPE, null, 4);

        assertThatThrownBy(() -> rope.rent(0)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> rope.restock(-1)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void blank_size_is_stored_as_null() {
        var grigri = Equipment.register("Grigri", EquipmentCategory.BELAY_DEVICE, "  ", 5);

        assertThat(grigri.size()).isNull();
    }
}
