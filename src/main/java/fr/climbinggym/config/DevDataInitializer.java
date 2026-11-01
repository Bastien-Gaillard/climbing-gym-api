package fr.climbinggym.config;

import fr.climbinggym.application.port.in.EquipmentUseCase;
import fr.climbinggym.application.port.in.MemberUseCase;
import fr.climbinggym.application.port.in.PlanSlotCommand;
import fr.climbinggym.application.port.in.RegisterEquipmentCommand;
import fr.climbinggym.application.port.in.RegisterMemberCommand;
import fr.climbinggym.application.port.in.SlotUseCase;
import fr.climbinggym.application.port.in.SubscribeCommand;
import fr.climbinggym.application.port.in.SubscriptionUseCase;
import fr.climbinggym.domain.equipment.EquipmentCategory;
import fr.climbinggym.domain.slot.SlotType;
import fr.climbinggym.domain.subscription.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Demo data for local development (profile "dev"). Goes through the use cases, never through SQL,
 * so seeded data always respects the business rules.
 */
@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private final MemberUseCase members;
    private final SubscriptionUseCase subscriptions;
    private final SlotUseCase slots;
    private final EquipmentUseCase equipment;
    private final Clock clock;

    public DevDataInitializer(MemberUseCase members, SubscriptionUseCase subscriptions, SlotUseCase slots,
                              EquipmentUseCase equipment, Clock clock) {
        this.members = members;
        this.subscriptions = subscriptions;
        this.slots = slots;
        this.equipment = equipment;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (members.search("").isEmpty()) seedMembers();
        if (equipment.findAll().isEmpty()) seedEquipment();
        seedNextTwoWeeksOfSlots();
    }

    private void seedMembers() {
        member("Léa", "Durand", "lea.durand@example.com", LocalDate.of(1994, 3, 12), true, SubscriptionType.ANNUAL);
        member("Hugo", "Martin", "hugo.martin@example.com", LocalDate.of(1988, 7, 22), false, SubscriptionType.MONTHLY);
        member("Chloé", "Bernard", "chloe.bernard@example.com", LocalDate.of(2013, 9, 1), false, SubscriptionType.TEN_ENTRIES);
        member("Louis", "Petit", "louis.petit@example.com", LocalDate.of(1979, 11, 30), true, SubscriptionType.TEN_ENTRIES);
        log.info("Dev data: members seeded");
    }

    private void member(String firstName, String lastName, String email, LocalDate birthDate, boolean belay, SubscriptionType type) {
        var member = members.register(new RegisterMemberCommand(firstName, lastName, email, birthDate, belay));
        subscriptions.subscribe(new SubscribeCommand(member.id(), type, null));
    }

    private void seedEquipment() {
        for (var size : new String[]{"38", "40", "42", "44"}) {
            equipment.register(new RegisterEquipmentCommand("Chaussons", EquipmentCategory.SHOES, size, 8));
        }
        equipment.register(new RegisterEquipmentCommand("Baudrier", EquipmentCategory.HARNESS, "S", 6));
        equipment.register(new RegisterEquipmentCommand("Baudrier", EquipmentCategory.HARNESS, "M", 10));
        equipment.register(new RegisterEquipmentCommand("Baudrier", EquipmentCategory.HARNESS, "L", 6));
        equipment.register(new RegisterEquipmentCommand("Grigri", EquipmentCategory.BELAY_DEVICE, null, 12));
        equipment.register(new RegisterEquipmentCommand("Casque", EquipmentCategory.HELMET, "Unique", 10));
        log.info("Dev data: equipment seeded");
    }

    private void seedNextTwoWeeksOfSlots() {
        var today = LocalDate.now(clock);
        var until = today.plusDays(14);
        if (!slots.findBetween(today.plusDays(1), until).isEmpty()) return;

        for (var day = today.plusDays(1); !day.isAfter(until); day = day.plusDays(1)) {
            plan(SlotType.BOULDER, day, LocalTime.of(12, 0), 2, 25);
            plan(SlotType.ROPE, day, LocalTime.of(18, 0), 3, 16);
            if (day.getDayOfWeek() == DayOfWeek.WEDNESDAY || day.getDayOfWeek() == DayOfWeek.SATURDAY) {
                plan(SlotType.KIDS, day, LocalTime.of(14, 0), 2, 12);
            }
            if (day.getDayOfWeek() == DayOfWeek.THURSDAY) {
                plan(SlotType.COURSE, day, LocalTime.of(19, 30), 2, 8);
            }
        }
        log.info("Dev data: slots planned until {}", until);
    }

    private void plan(SlotType type, LocalDate day, LocalTime start, int hours, int capacity) {
        var startsAt = day.atTime(start);
        slots.plan(new PlanSlotCommand(type, startsAt, startsAt.plusHours(hours), capacity));
    }
}
