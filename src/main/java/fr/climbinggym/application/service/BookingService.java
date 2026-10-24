package fr.climbinggym.application.service;

import fr.climbinggym.application.UseCase;
import fr.climbinggym.application.port.in.BookingUseCase;
import fr.climbinggym.application.port.out.BookingRepository;
import fr.climbinggym.application.port.out.MemberRepository;
import fr.climbinggym.application.port.out.SlotRepository;
import fr.climbinggym.application.port.out.SubscriptionRepository;
import fr.climbinggym.domain.booking.Booking;
import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.shared.NotFoundException;
import fr.climbinggym.domain.slot.Slot;
import fr.climbinggym.domain.subscription.Subscription;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@UseCase
public class BookingService implements BookingUseCase {

    private final BookingRepository bookings;
    private final SlotRepository slots;
    private final MemberRepository members;
    private final SubscriptionRepository subscriptions;
    private final Clock clock;

    public BookingService(BookingRepository bookings, SlotRepository slots, MemberRepository members,
                          SubscriptionRepository subscriptions, Clock clock) {
        this.bookings = bookings;
        this.slots = slots;
        this.members = members;
        this.subscriptions = subscriptions;
        this.clock = clock;
    }

    @Override
    public Booking book(UUID slotId, UUID memberId) {
        var slot = slots.findById(slotId).orElseThrow(() -> new NotFoundException("Slot", slotId));
        var member = members.findById(memberId).orElseThrow(() -> new NotFoundException("Member", memberId));
        var now = LocalDateTime.now(clock);

        if (bookings.existsConfirmed(slotId, memberId)) {
            throw new BusinessRuleException("ALREADY_BOOKED", "Member already booked this slot");
        }
        slot.checkBookableBy(member, bookings.countConfirmed(slotId), now);

        var subscription = findUsableSubscription(memberId, slot);
        subscription.consumeEntry();
        subscriptions.save(subscription);

        return bookings.save(Booking.confirm(slot.id(), member.id(), subscription.id(), now));
    }

    @Override
    public Booking cancel(UUID bookingId) {
        var booking = bookings.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking", bookingId));
        var slot = slots.findById(booking.slotId()).orElseThrow(() -> new NotFoundException("Slot", booking.slotId()));

        booking.cancel(slot, LocalDateTime.now(clock));
        subscriptions.findById(booking.subscriptionId()).ifPresent(subscription -> {
            subscription.refundEntry();
            subscriptions.save(subscription);
        });

        return bookings.save(booking);
    }

    @Override
    public List<Booking> findByMember(UUID memberId) {
        return bookings.findByMemberId(memberId);
    }

    /**
     * Time-based subscriptions are used first so that entry cards are only consumed when needed.
     */
    private Subscription findUsableSubscription(UUID memberId, Slot slot) {
        var day = slot.startsAt().toLocalDate();
        return subscriptions.findByMemberId(memberId).stream()
                .filter(subscription -> subscription.isValidOn(day))
                .min(Comparator.comparing((Subscription subscription) -> subscription.type().isEntryBased()))
                .orElseThrow(() -> new BusinessRuleException("NO_VALID_SUBSCRIPTION", "Member has no valid subscription on " + day));
    }
}
