package fr.climbinggym.application.port.out;

import fr.climbinggym.domain.subscription.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findById(UUID id);

    List<Subscription> findByMemberId(UUID memberId);
}
