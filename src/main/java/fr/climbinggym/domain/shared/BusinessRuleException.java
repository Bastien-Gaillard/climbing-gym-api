package fr.climbinggym.domain.shared;

public class BusinessRuleException extends DomainException {

    public BusinessRuleException(String code, String message) {
        super(code, message);
    }
}
