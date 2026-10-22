package fr.climbinggym.adapter.in.web.member;

import fr.climbinggym.application.port.in.UpdateMemberCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateMemberRequest(
        @Size(min = 1, max = 100) String firstName,
        @Size(min = 1, max = 100) String lastName,
        @Email String email,
        Boolean belayCertified
) {

    UpdateMemberCommand toCommand() {
        return new UpdateMemberCommand(firstName, lastName, email, belayCertified);
    }
}
