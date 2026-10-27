package fr.climbinggym.adapter.in.batch;

/**
 * Raw line of the members CSV, before any parsing.
 */
public record MemberCsvLine(
        String lastName,
        String firstName,
        String email,
        String birthDate,
        String belayCertified,
        String subscriptionType
) {

    static final String[] COLUMNS = {"lastName", "firstName", "email", "birthDate", "belayCertified", "subscriptionType"};
}
