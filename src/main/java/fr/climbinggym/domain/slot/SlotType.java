package fr.climbinggym.domain.slot;

public enum SlotType {
    BOULDER,
    ROPE,
    COURSE,
    KIDS;

    public boolean requiresBelayCertification() {
        return this == ROPE;
    }

    public boolean isKidsOnly() {
        return this == KIDS;
    }
}
