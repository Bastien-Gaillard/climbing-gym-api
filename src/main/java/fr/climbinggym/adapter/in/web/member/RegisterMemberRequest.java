package fr.climbinggym.adapter.in.web.member;

import fr.climbinggym.application.port.in.RegisterMemberCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterMemberRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email String email,
        @NotNull @Past LocalDate birthDate,
        boolean belayCertified
) {

    RegisterMemberCommand toCommand() {
        return new RegisterMemberCommand(firstName, lastName, email, birthDate, belayCertified);
    }
}
