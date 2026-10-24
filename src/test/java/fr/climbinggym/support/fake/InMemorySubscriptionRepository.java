package fr.climbinggym.support.fake;

import fr.climbinggym.application.port.out.SubscriptionRepository;
import fr.climbinggym.domain.subscription.Subscription;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemorySubscriptionRepository implements SubscriptionRepository {

    private final Map<UUID, Subscription> store = new LinkedHashMap<>();

    @Override
    public Subscription save(Subscription subscription) {
        store.put(subscription.id(), subscription);
        return subscription;
    }

    @Override
    public Optional<Subscription> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Subscription> findByMemberId(UUID memberId) {
        return store.values().stream().filter(s -> s.memberId().equals(memberId)).toList();
    }
}
