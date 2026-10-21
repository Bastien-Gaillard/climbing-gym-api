package fr.climbinggym.application.port.in;

import java.time.LocalDate;

public record RegisterMemberCommand(
        String firstName,
        String lastName,
        String email,
        LocalDate birthDate,
        boolean belayCertified
) {
}
