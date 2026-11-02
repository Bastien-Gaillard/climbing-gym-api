package fr.climbinggym.adapter.out.persistence.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {

    List<SubscriptionEntity> findByMemberIdOrderByStartDateDesc(UUID memberId);
}
