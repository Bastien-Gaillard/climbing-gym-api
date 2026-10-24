package fr.climbinggym.config;

import fr.climbinggym.application.UseCase;
import fr.climbinggym.application.port.out.MemberRepository;
import fr.climbinggym.application.port.out.SlotRepository;
import fr.climbinggym.application.port.out.SubscriptionRepository;
import fr.climbinggym.application.service.MemberService;
import fr.climbinggym.application.service.SlotService;
import fr.climbinggym.application.service.SubscriptionService;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.time.Clock;
import java.util.List;

/**
 * Wires the application layer without putting Spring annotations in it.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static Advisor useCaseTransactionAdvisor(TransactionManager transactionManager) {
        var attribute = new RuleBasedTransactionAttribute();
        attribute.setRollbackRules(List.of(new RollbackRuleAttribute(Exception.class)));

        var source = new MatchAlwaysTransactionAttributeSource();
        source.setTransactionAttribute(attribute);

        return new DefaultPointcutAdvisor(
                new AnnotationMatchingPointcut(UseCase.class, true),
                new TransactionInterceptor(transactionManager, source)
        );
    }

    @Bean
    public MemberService memberService(MemberRepository memberRepository, Clock clock) {
        return new MemberService(memberRepository, clock);
    }

    @Bean
    public SubscriptionService subscriptionService(SubscriptionRepository subscriptionRepository,
                                                   MemberRepository memberRepository, Clock clock) {
        return new SubscriptionService(subscriptionRepository, memberRepository, clock);
    }

    @Bean
    public SlotService slotService(SlotRepository slotRepository, Clock clock) {
        return new SlotService(slotRepository, clock);
    }
}
