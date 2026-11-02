package fr.climbinggym.adapter.out.persistence.member;

import fr.climbinggym.application.port.out.MemberRepository;
import fr.climbinggym.domain.member.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class MemberPersistenceAdapter implements MemberRepository {

    private final MemberJpaRepository jpa;

    MemberPersistenceAdapter(MemberJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Member save(Member member) {
        jpa.save(MemberMapper.toEntity(member));
        return member;
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return jpa.findById(id).map(MemberMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmailIgnoreCase(email);
    }

    @Override
    public List<Member> search(String query) {
        var entities = query.isBlank() ? jpa.findAllByOrderByLastNameAscFirstNameAsc() : jpa.search(query);
        return entities.stream().map(MemberMapper::toDomain).toList();
    }
}
