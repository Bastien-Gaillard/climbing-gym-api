package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.subscription.SubscriptionType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @param startDate optional, defaults to today
 */
public record SubscribeCommand(UUID memberId, SubscriptionType type, LocalDate startDate) {
}
