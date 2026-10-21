package fr.climbinggym.domain.shared;

public class NotFoundException extends DomainException {

    public NotFoundException(String resource, Object id) {
        super(resource.toUpperCase() + "_NOT_FOUND", resource + " " + id + " not found");
    }
}
