package fr.climbinggym.domain.subscription;

import fr.climbinggym.domain.shared.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubscriptionTest {

    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final LocalDate START = LocalDate.of(2026, 11, 1);

    @Test
    void monthly_subscription_ends_the_day_before_next_month() {
        var subscription = Subscription.subscribe(MEMBER_ID, SubscriptionType.MONTHLY, START);

        assertThat(subscription.endDate()).isEqualTo(LocalDate.of(2026, 11, 30));
        assertThat(subscription.remainingEntries()).isNull();
        assertThat(subscription.isValidOn(LocalDate.of(2026, 11, 30))).isTrue();
        assertThat(subscription.isValidOn(LocalDate.of(2026, 12, 1))).isFalse();
        assertThat(subscription.isValidOn(LocalDate.of(2026, 10, 31))).isFalse();
    }

    @Test
    void ten_entries_card_is_invalid_once_all_entries_are_used() {
        var card = Subscription.subscribe(MEMBER_ID, SubscriptionType.TEN_ENTRIES, START);

        for (int i = 0; i < 10; i++) card.consumeEntry();

        assertThat(card.remainingEntries()).isZero();
        assertThat(card.isValidOn(START)).isFalse();
        assertThatThrownBy(card::consumeEntry)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no entry left");
    }

    @Test
    void refund_never_exceeds_the_initial_number_of_entries() {
        var card = Subscription.subscribe(MEMBER_ID, SubscriptionType.TEN_ENTRIES, START);

        card.refundEntry();

        assertThat(card.remainingEntries()).isEqualTo(10);
    }

    @Test
    void time_based_subscription_ignores_entries() {
        var annual = Subscription.subscribe(MEMBER_ID, SubscriptionType.ANNUAL, START);

        annual.consumeEntry();

        assertThat(annual.remainingEntries()).isNull();
        assertThat(annual.isValidOn(LocalDate.of(2027, 10, 31))).isTrue();
    }

    @Test
    void cancelled_subscription_is_never_valid() {
        var subscription = Subscription.subscribe(MEMBER_ID, SubscriptionType.MONTHLY, START);

        subscription.cancel();

        assertThat(subscription.isValidOn(START)).isFalse();
        assertThatThrownBy(subscription::cancel).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void detects_overlapping_periods() {
        var november = Subscription.subscribe(MEMBER_ID, SubscriptionType.MONTHLY, START);
        var midNovember = Subscription.subscribe(MEMBER_ID, SubscriptionType.MONTHLY, LocalDate.of(2026, 11, 15));
        var december = Subscription.subscribe(MEMBER_ID, SubscriptionType.MONTHLY, LocalDate.of(2026, 12, 1));

        assertThat(november.overlaps(midNovember)).isTrue();
        assertThat(november.overlaps(december)).isFalse();
    }
}
