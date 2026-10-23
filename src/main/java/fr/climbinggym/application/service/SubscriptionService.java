package fr.climbinggym.application.service;

import fr.climbinggym.application.UseCase;
import fr.climbinggym.application.port.in.SubscribeCommand;
import fr.climbinggym.application.port.in.SubscriptionUseCase;
import fr.climbinggym.application.port.out.MemberRepository;
import fr.climbinggym.application.port.out.SubscriptionRepository;
import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.shared.NotFoundException;
import fr.climbinggym.domain.subscription.Subscription;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@UseCase
public class SubscriptionService implements SubscriptionUseCase {

    private final SubscriptionRepository subscriptions;
    private final MemberRepository members;
    private final Clock clock;

    public SubscriptionService(SubscriptionRepository subscriptions, MemberRepository members, Clock clock) {
        this.subscriptions = subscriptions;
        this.members = members;
        this.clock = clock;
    }

    @Override
    public Subscription subscribe(SubscribeCommand command) {
        if (members.findById(command.memberId()).isEmpty()) throw new NotFoundException("Member", command.memberId());

        var startDate = command.startDate() == null ? LocalDate.now(clock) : command.startDate();
        var subscription = Subscription.subscribe(command.memberId(), command.type(), startDate);

        if (!subscription.type().isEntryBased()) ensureNoOverlap(subscription);
        return subscriptions.save(subscription);
    }

    @Override
    public Subscription cancel(UUID subscriptionId) {
        var subscription = subscriptions.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription", subscriptionId));
        subscription.cancel();
        return subscriptions.save(subscription);
    }

    @Override
    public List<Subscription> findByMember(UUID memberId) {
        return subscriptions.findByMemberId(memberId);
    }

    private void ensureNoOverlap(Subscription candidate) {
        var overlapping = subscriptions.findByMemberId(candidate.memberId()).stream()
                .filter(Subscription::isActive)
                .filter(existing -> !existing.type().isEntryBased())
                .anyMatch(candidate::overlaps);

        if (overlapping) {
            throw new BusinessRuleException("SUBSCRIPTION_OVERLAP", "Member already has an active subscription for this period");
        }
    }
}
