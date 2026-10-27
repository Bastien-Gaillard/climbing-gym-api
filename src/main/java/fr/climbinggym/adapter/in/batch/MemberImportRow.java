package fr.climbinggym.adapter.in.batch;

import fr.climbinggym.application.port.in.RegisterMemberCommand;
import fr.climbinggym.domain.subscription.SubscriptionType;

/**
 * @param subscriptionType nullable: the member is imported without subscription
 */
public record MemberImportRow(RegisterMemberCommand member, SubscriptionType subscriptionType) {
}
