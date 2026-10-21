package fr.climbinggym.domain.member;

import fr.climbinggym.domain.shared.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 21, 19, 0);

    @Test
    void register_normalizes_email_and_trims_names() {
        var member = Member.register("  Alex ", " Honnold", " Alex.Honnold@Mail.COM ", LocalDate.of(1985, 8, 17), true, NOW);

        assertThat(member.id()).isNotNull();
        assertThat(member.firstName()).isEqualTo("Alex");
        assertThat(member.lastName()).isEqualTo("Honnold");
        assertThat(member.email()).isEqualTo("alex.honnold@mail.com");
        assertThat(member.createdAt()).isEqualTo(NOW);
    }

    @Test
    void register_rejects_invalid_email() {
        assertThatThrownBy(() -> Member.register("Alex", "Honnold", "not-an-email", LocalDate.of(1985, 8, 17), false, NOW))
                .isInstanceOf(InvalidValueException.class)
                .hasMessageContaining("email");
    }

    @Test
    void register_rejects_birth_date_in_the_future() {
        assertThatThrownBy(() -> Member.register("Alex", "Honnold", "alex@mail.com", LocalDate.of(2030, 1, 1), false, NOW))
                .isInstanceOf(InvalidValueException.class)
                .hasMessageContaining("birthDate");
    }

    @Test
    void update_only_changes_provided_fields() {
        var member = Member.register("Alex", "Honnold", "alex@mail.com", LocalDate.of(1985, 8, 17), false, NOW);

        member.update(null, "Megos", null, true);

        assertThat(member.firstName()).isEqualTo("Alex");
        assertThat(member.lastName()).isEqualTo("Megos");
        assertThat(member.email()).isEqualTo("alex@mail.com");
        assertThat(member.belayCertified()).isTrue();
    }

    @Test
    void is_minor_until_eighteenth_birthday() {
        var member = Member.register("Janja", "Garnbret", "janja@mail.com", LocalDate.of(2010, 3, 12), false, NOW);

        assertThat(member.isMinorOn(LocalDate.of(2028, 3, 11))).isTrue();
        assertThat(member.isMinorOn(LocalDate.of(2028, 3, 12))).isFalse();
    }
}
