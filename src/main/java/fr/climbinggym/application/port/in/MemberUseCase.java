package fr.climbinggym.application.port.in;

import fr.climbinggym.domain.member.Member;

import java.util.List;
import java.util.UUID;

public interface MemberUseCase {

    Member register(RegisterMemberCommand command);

    Member update(UUID id, UpdateMemberCommand command);

    Member findById(UUID id);

    List<Member> search(String query);
}
