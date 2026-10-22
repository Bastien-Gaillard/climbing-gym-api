package fr.climbinggym.adapter.in.web.member;

import fr.climbinggym.domain.member.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MemberResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        LocalDate birthDate,
        boolean belayCertified,
        LocalDateTime createdAt
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.id(),
                member.firstName(),
                member.lastName(),
                member.email(),
                member.birthDate(),
                member.belayCertified(),
                member.createdAt()
        );
    }
}
