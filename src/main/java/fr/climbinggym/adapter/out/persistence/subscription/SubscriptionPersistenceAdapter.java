package fr.climbinggym.adapter.out.persistence.subscription;

import fr.climbinggym.application.port.out.SubscriptionRepository;
import fr.climbinggym.domain.subscription.Subscription;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class SubscriptionPersistenceAdapter implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpa;

    SubscriptionPersistenceAdapter(SubscriptionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Subscription save(Subscription subscription) {
        jpa.save(toEntity(subscription));
        return subscription;
    }

    @Override
    public Optional<Subscription> findById(UUID id) {
        return jpa.findById(id).map(SubscriptionPersistenceAdapter::toDomain);
    }

    @Override
    public List<Subscription> findByMemberId(UUID memberId) {
        return jpa.findByMemberIdOrderByStartDateDesc(memberId).stream()
                .map(SubscriptionPersistenceAdapter::toDomain)
                .toList();
    }

    private static SubscriptionEntity toEntity(Subscription s) {
        return new SubscriptionEntity(s.id(), s.memberId(), s.type(), s.startDate(), s.endDate(), s.remainingEntries(), s.status());
    }

    private static Subscription toDomain(SubscriptionEntity e) {
        return Subscription.restore(e.getId(), e.getMemberId(), e.getType(), e.getStartDate(), e.getEndDate(),
                e.getRemainingEntries(), e.getStatus());
    }
}
