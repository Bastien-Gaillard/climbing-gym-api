package fr.climbinggym.adapter.in.batch;

import fr.climbinggym.application.port.in.MemberUseCase;
import fr.climbinggym.application.port.in.SubscribeCommand;
import fr.climbinggym.application.port.in.SubscriptionUseCase;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

/**
 * Goes through the use cases so imported members follow the same business rules as the API.
 */
public class MemberImportWriter implements ItemWriter<MemberImportRow> {

    private final MemberUseCase members;
    private final SubscriptionUseCase subscriptions;

    public MemberImportWriter(MemberUseCase members, SubscriptionUseCase subscriptions) {
        this.members = members;
        this.subscriptions = subscriptions;
    }

    @Override
    public void write(Chunk<? extends MemberImportRow> chunk) {
        for (var row : chunk) {
            var member = members.register(row.member());
            if (row.subscriptionType() == null) continue;
            subscriptions.subscribe(new SubscribeCommand(member.id(), row.subscriptionType(), null));
        }
    }
}
