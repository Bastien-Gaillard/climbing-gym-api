package fr.climbinggym.adapter.in.web.subscription;

import fr.climbinggym.domain.subscription.Subscription;
import fr.climbinggym.domain.subscription.SubscriptionStatus;
import fr.climbinggym.domain.subscription.SubscriptionType;

import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID memberId,
        SubscriptionType type,
        LocalDate startDate,
        LocalDate endDate,
        Integer remainingEntries,
        SubscriptionStatus status
) {

    public static SubscriptionResponse from(Subscription s) {
        return new SubscriptionResponse(s.id(), s.memberId(), s.type(), s.startDate(), s.endDate(), s.remainingEntries(), s.status());
    }
}
