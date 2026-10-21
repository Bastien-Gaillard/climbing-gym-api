package fr.climbinggym.domain.shared;

public class InvalidValueException extends DomainException {

    private final String field;

    public InvalidValueException(String field, String message) {
        super("INVALID_VALUE", field + " " + message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
