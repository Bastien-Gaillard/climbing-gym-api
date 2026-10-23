package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.subscription.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionUseCase {

    Subscription subscribe(SubscribeCommand command);

    Subscription cancel(UUID subscriptionId);

    List<Subscription> findByMember(UUID memberId);
}
