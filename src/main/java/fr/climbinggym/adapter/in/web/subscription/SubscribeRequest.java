package fr.climbinggym.adapter.in.web.subscription;

import fr.climbinggym.domain.subscription.SubscriptionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SubscribeRequest(@NotNull SubscriptionType type, LocalDate startDate) {
}
