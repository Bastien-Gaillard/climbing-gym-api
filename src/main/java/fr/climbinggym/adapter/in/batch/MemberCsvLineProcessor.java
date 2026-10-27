package fr.climbinggym.adapter.in.batch;

import fr.climbinggym.application.port.in.MemberUseCase;
import fr.climbinggym.application.port.in.RegisterMemberCommand;
import fr.climbinggym.domain.subscription.SubscriptionType;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Parses a raw CSV line. Returns null (filtered) when the email is already known,
 * either in database or earlier in the same file.
 */
public class MemberCsvLineProcessor implements ItemProcessor<MemberCsvLine, MemberImportRow> {

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );
    private static final Set<String> TRUE_VALUES = Set.of("true", "oui", "yes", "1", "x");
    private static final Map<String, SubscriptionType> TYPE_ALIASES = Map.of(
            "MENSUEL", SubscriptionType.MONTHLY,
            "ANNUEL", SubscriptionType.ANNUAL,
            "CARTE10", SubscriptionType.TEN_ENTRIES
    );

    private final MemberUseCase members;
    private final Set<String> seenEmails = new HashSet<>();

    public MemberCsvLineProcessor(MemberUseCase members) {
        this.members = members;
    }

    @Override
    public MemberImportRow process(MemberCsvLine line) {
        if (isBlank(line.email())) throw new InvalidCsvLineException("email is missing");

        var email = line.email().strip().toLowerCase(Locale.ROOT);
        if (!seenEmails.add(email) || members.isEmailUsed(email)) return null;

        var command = new RegisterMemberCommand(
                line.firstName(),
                line.lastName(),
                email,
                parseDate(line.birthDate()),
                parseBoolean(line.belayCertified())
        );
        return new MemberImportRow(command, parseSubscriptionType(line.subscriptionType()));
    }

    private static LocalDate parseDate(String value) {
        if (isBlank(value)) throw new InvalidCsvLineException("birthDate is missing");

        for (var format : DATE_FORMATS) {
            try {
                return LocalDate.parse(value.strip(), format);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }
        throw new InvalidCsvLineException("birthDate '" + value + "' is not a valid date");
    }

    private static boolean parseBoolean(String value) {
        return !isBlank(value) && TRUE_VALUES.contains(value.strip().toLowerCase(Locale.ROOT));
    }

    private static SubscriptionType parseSubscriptionType(String value) {
        if (isBlank(value)) return null;

        var key = value.strip().toUpperCase(Locale.ROOT);
        if (TYPE_ALIASES.containsKey(key)) return TYPE_ALIASES.get(key);
        try {
            return SubscriptionType.valueOf(key);
        } catch (IllegalArgumentException e) {
            throw new InvalidCsvLineException("unknown subscription type '" + value + "'");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
