package fr.climbinggym.domain.slot;

import fr.climbinggym.domain.shared.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlotTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 24, 10, 0);
    private static final LocalDateTime EVENING = LocalDateTime.of(2026, 10, 26, 18, 0);

    @Test
    void plans_a_valid_slot() {
        var slot = Slot.plan(SlotType.BOULDER, EVENING, EVENING.plusHours(2), 30, NOW);

        assertThat(slot.id()).isNotNull();
        assertThat(slot.remainingPlaces(12)).isEqualTo(18);
        assertThat(slot.remainingPlaces(40)).isZero();
    }

    @Test
    void rejects_end_before_start() {
        assertThatThrownBy(() -> Slot.plan(SlotType.ROPE, EVENING, EVENING.minusMinutes(1), 10, NOW))
                .isInstanceOf(InvalidValueException.class)
                .hasMessageContaining("endsAt");
    }

    @Test
    void rejects_slot_in_the_past() {
        assertThatThrownBy(() -> Slot.plan(SlotType.ROPE, NOW.minusHours(1), NOW.plusHours(1), 10, NOW))
                .isInstanceOf(InvalidValueException.class)
                .hasMessageContaining("startsAt");
    }

    @Test
    void rejects_too_long_slot() {
        assertThatThrownBy(() -> Slot.plan(SlotType.COURSE, EVENING, EVENING.plusHours(5), 10, NOW))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejects_non_positive_capacity() {
        assertThatThrownBy(() -> Slot.plan(SlotType.KIDS, EVENING, EVENING.plusHours(1), 0, NOW))
                .isInstanceOf(InvalidValueException.class)
                .hasMessageContaining("capacity");
    }

    @Test
    void has_started_from_its_start_time() {
        var slot = Slot.plan(SlotType.BOULDER, EVENING, EVENING.plusHours(2), 30, NOW);

        assertThat(slot.hasStartedAt(EVENING.minusSeconds(1))).isFalse();
        assertThat(slot.hasStartedAt(EVENING)).isTrue();
    }
}
