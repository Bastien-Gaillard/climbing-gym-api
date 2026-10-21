package fr.climbinggym.domain.shared;

public final class Guard {

    private Guard() {
    }

    public static <T> T notNull(T value, String field) {
        if (value == null) throw new InvalidValueException(field, "is required");
        return value;
    }

    public static String notBlank(String value, String field) {
        if (value == null || value.isBlank()) throw new InvalidValueException(field, "must not be blank");
        return value.strip();
    }

    public static int positive(int value, String field) {
        if (value <= 0) throw new InvalidValueException(field, "must be positive");
        return value;
    }
}
