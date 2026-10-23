package fr.climbinggym.domain.subscription;

public enum SubscriptionType {
    MONTHLY(1, null),
    ANNUAL(12, null),
    TEN_ENTRIES(12, 10);

    private final int validityMonths;
    private final Integer entries;

    SubscriptionType(int validityMonths, Integer entries) {
        this.validityMonths = validityMonths;
        this.entries = entries;
    }

    public int validityMonths() {
        return validityMonths;
    }

    public Integer entries() {
        return entries;
    }

    public boolean isEntryBased() {
        return entries != null;
    }
}
