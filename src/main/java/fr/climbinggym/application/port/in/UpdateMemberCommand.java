package fr.climbinggym.application.port.in;

public record UpdateMemberCommand(
        String firstName,
        String lastName,
        String email,
        Boolean belayCertified
) {
}
