package fr.climbinggym.support.fake;

import fr.climbinggym.application.port.out.MemberRepository;
import fr.climbinggym.domain.member.Member;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<UUID, Member> store = new LinkedHashMap<>();

    @Override
    public Member save(Member member) {
        store.put(member.id(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream().anyMatch(member -> member.email().equalsIgnoreCase(email));
    }

    @Override
    public List<Member> search(String query) {
        return store.values().stream()
                .filter(member -> query.isBlank() || member.email().contains(query.toLowerCase()))
                .toList();
    }
}
