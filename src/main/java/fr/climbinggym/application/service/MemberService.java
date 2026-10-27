package fr.climbinggym.application.service;

import fr.climbinggym.application.UseCase;
import fr.climbinggym.application.port.in.MemberUseCase;
import fr.climbinggym.application.port.in.RegisterMemberCommand;
import fr.climbinggym.application.port.in.UpdateMemberCommand;
import fr.climbinggym.application.port.out.MemberRepository;
import fr.climbinggym.domain.member.Member;
import fr.climbinggym.domain.shared.BusinessRuleException;
import fr.climbinggym.domain.shared.NotFoundException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@UseCase
public class MemberService implements MemberUseCase {

    private final MemberRepository members;
    private final Clock clock;

    public MemberService(MemberRepository members, Clock clock) {
        this.members = members;
        this.clock = clock;
    }

    @Override
    public Member register(RegisterMemberCommand command) {
        var member = Member.register(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.birthDate(),
                command.belayCertified(),
                LocalDateTime.now(clock)
        );
        ensureEmailIsFree(member.email());
        return members.save(member);
    }

    @Override
    public Member update(UUID id, UpdateMemberCommand command) {
        var member = findById(id);
        var previousEmail = member.email();

        member.update(command.firstName(), command.lastName(), command.email(), command.belayCertified());
        if (!member.email().equals(previousEmail)) ensureEmailIsFree(member.email());

        return members.save(member);
    }

    @Override
    public Member findById(UUID id) {
        return members.findById(id).orElseThrow(() -> new NotFoundException("Member", id));
    }

    @Override
    public List<Member> search(String query) {
        return members.search(query == null ? "" : query.strip());
    }

    @Override
    public boolean isEmailUsed(String email) {
        return members.existsByEmail(Member.normalizeEmail(email));
    }

    private void ensureEmailIsFree(String email) {
        if (members.existsByEmail(email)) {
            throw new BusinessRuleException("EMAIL_ALREADY_USED", "A member already uses " + email);
        }
    }
}
