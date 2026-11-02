package fr.climbinggym.application.port.out;

import fr.climbinggym.domain.member.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(UUID id);

    boolean existsByEmail(String email);

    List<Member> search(String query);
}
