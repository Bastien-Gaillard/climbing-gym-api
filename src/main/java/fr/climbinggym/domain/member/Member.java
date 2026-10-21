package fr.climbinggym.domain.member;

import fr.climbinggym.domain.shared.Guard;
import fr.climbinggym.domain.shared.InvalidValueException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Member {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int ADULT_AGE = 18;

    private final UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private final LocalDate birthDate;
    private boolean belayCertified;
    private final LocalDateTime createdAt;

    private Member(UUID id, String firstName, String lastName, String email, LocalDate birthDate,
                   boolean belayCertified, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.belayCertified = belayCertified;
        this.createdAt = createdAt;
    }

    public static Member register(String firstName, String lastName, String email, LocalDate birthDate,
                                  boolean belayCertified, LocalDateTime now) {
        Guard.notNull(birthDate, "birthDate");
        if (!birthDate.isBefore(now.toLocalDate())) throw new InvalidValueException("birthDate", "must be in the past");

        return new Member(
                UUID.randomUUID(),
                Guard.notBlank(firstName, "firstName"),
                Guard.notBlank(lastName, "lastName"),
                normalizeEmail(email),
                birthDate,
                belayCertified,
                now
        );
    }

    public static Member restore(UUID id, String firstName, String lastName, String email, LocalDate birthDate,
                                 boolean belayCertified, LocalDateTime createdAt) {
        return new Member(id, firstName, lastName, email, birthDate, belayCertified, createdAt);
    }

    public void update(String firstName, String lastName, String email, Boolean belayCertified) {
        if (firstName != null) this.firstName = Guard.notBlank(firstName, "firstName");
        if (lastName != null) this.lastName = Guard.notBlank(lastName, "lastName");
        if (email != null) this.email = normalizeEmail(email);
        if (belayCertified != null) this.belayCertified = belayCertified;
    }

    public boolean isMinorOn(LocalDate date) {
        return Period.between(birthDate, date).getYears() < ADULT_AGE;
    }

    public static String normalizeEmail(String email) {
        var value = Guard.notBlank(email, "email").toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(value).matches()) throw new InvalidValueException("email", "is not a valid email");
        return value;
    }

    public UUID id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String email() {
        return email;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public boolean belayCertified() {
        return belayCertified;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}
