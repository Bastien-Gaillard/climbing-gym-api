package fr.climbinggym.application.service;

import fr.climbinggym.domain.booking.BookingStatus;
import fr.climbinggym.domain.member.Member;
import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.slot.Slot;
import fr.climbinggym.domain.slot.SlotType;
import fr.climbinggym.domain.subscription.Subscription;
import fr.climbinggym.domain.subscription.SubscriptionType;
import fr.climbinggym.support.fake.InMemoryBookingRepository;
import fr.climbinggym.support.fake.InMemoryMemberRepository;
import fr.climbinggym.support.fake.InMemorySlotRepository;
import fr.climbinggym.support.fake.InMemorySubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingServiceTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 24, 15, 0);
    private static final LocalDateTime SLOT_START = LocalDateTime.of(2026, 10, 27, 18, 30);

    private final InMemoryBookingRepository bookings = new InMemoryBookingRepository();
    private final InMemorySlotRepository slots = new InMemorySlotRepository();
    private final InMemoryMemberRepository members = new InMemoryMemberRepository();
    private final InMemorySubscriptionRepository subscriptions = new InMemorySubscriptionRepository();

    private BookingService service;

    @BeforeEach
    void setUp() {
        service = serviceAt(NOW);
    }

    private BookingService serviceAt(LocalDateTime now) {
        var clock = Clock.fixed(now.atZone(PARIS).toInstant(), PARIS);
        return new BookingService(bookings, slots, members, subscriptions, clock);
    }

    @Test
    void books_a_slot_with_a_valid_subscription() {
        var member = adult(false);
        subscribe(member, SubscriptionType.MONTHLY);
        var slot = slot(SlotType.BOULDER, 10);

        var booking = service.book(slot.id(), member.id());

        assertThat(booking.status()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(bookings.countConfirmed(slot.id())).isEqualTo(1);
    }

    @Test
    void refuses_when_member_has_no_valid_subscription() {
        var member = adult(false);
        var slot = slot(SlotType.BOULDER, 10);

        assertThatThrownBy(() -> service.book(slot.id(), member.id()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no valid subscription");
    }

    @Test
    void refuses_when_slot_is_full() {
        var slot = slot(SlotType.BOULDER, 1);
        var first = adult(false);
        var second = adult(false);
        subscribe(first, SubscriptionType.MONTHLY);
        subscribe(second, SubscriptionType.MONTHLY);
        service.book(slot.id(), first.id());

        assertThatThrownBy(() -> service.book(slot.id(), second.id()))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("code").isEqualTo("SLOT_FULL");
    }

    @Test
    void refuses_a_double_booking() {
        var member = adult(false);
        subscribe(member, SubscriptionType.ANNUAL);
        var slot = slot(SlotType.BOULDER, 10);
        service.book(slot.id(), member.id());

        assertThatThrownBy(() -> service.book(slot.id(), member.id()))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("code").isEqualTo("ALREADY_BOOKED");
    }

    @Test
    void rope_slot_requires_belay_certification() {
        var member = adult(false);
        subscribe(member, SubscriptionType.MONTHLY);
        var slot = slot(SlotType.ROPE, 10);

        assertThatThrownBy(() -> service.book(slot.id(), member.id()))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("code").isEqualTo("BELAY_CERTIFICATION_REQUIRED");
    }

    @Test
    void kids_slot_is_reserved_to_minors() {
        var adult = adult(true);
        subscribe(adult, SubscriptionType.MONTHLY);
        var kid = save(Member.register("Lou", "Petit", "lou@mail.com", LocalDate.of(2015, 6, 1), false, NOW));
        subscribe(kid, SubscriptionType.MONTHLY);
        var slot = slot(SlotType.KIDS, 10);

        assertThat(service.book(slot.id(), kid.id()).isConfirmed()).isTrue();
        assertThatThrownBy(() -> service.book(slot.id(), adult.id()))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("code").isEqualTo("KIDS_ONLY_SLOT");
    }

    @Test
    void prefers_time_based_subscription_over_entry_card() {
        var member = adult(false);
        var card = subscribe(member, SubscriptionType.TEN_ENTRIES);
        var monthly = subscribe(member, SubscriptionType.MONTHLY);
        var slot = slot(SlotType.BOULDER, 10);

        var booking = service.book(slot.id(), member.id());

        assertThat(booking.subscriptionId()).isEqualTo(monthly.id());
        assertThat(card.remainingEntries()).isEqualTo(10);
    }

    @Test
    void entry_card_is_consumed_then_refunded_on_cancellation() {
        var member = adult(false);
        var card = subscribe(member, SubscriptionType.TEN_ENTRIES);
        var slot = slot(SlotType.BOULDER, 10);

        var booking = service.book(slot.id(), member.id());
        assertThat(card.remainingEntries()).isEqualTo(9);

        service.cancel(booking.id());
        assertThat(card.remainingEntries()).isEqualTo(10);
        assertThat(bookings.countConfirmed(slot.id())).isZero();
    }

    @Test
    void cannot_cancel_once_the_slot_has_started() {
        var member = adult(false);
        subscribe(member, SubscriptionType.MONTHLY);
        var slot = slot(SlotType.BOULDER, 10);
        var booking = service.book(slot.id(), member.id());

        var later = serviceAt(SLOT_START.plusMinutes(5));

        assertThatThrownBy(() -> later.cancel(booking.id()))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("code").isEqualTo("SLOT_ALREADY_STARTED");
    }

    private Member adult(boolean belayCertified) {
        var email = "climber-" + members.search("").size() + "@mail.com";
        return save(Member.register("Alex", "Megos", email, LocalDate.of(1993, 8, 12), belayCertified, NOW));
    }

    private Member save(Member member) {
        return members.save(member);
    }

    private Subscription subscribe(Member member, SubscriptionType type) {
        return subscriptions.save(Subscription.subscribe(member.id(), type, NOW.toLocalDate()));
    }

    private Slot slot(SlotType type, int capacity) {
        return slots.save(Slot.plan(type, SLOT_START, SLOT_START.plusHours(2), capacity, NOW));
    }
}
