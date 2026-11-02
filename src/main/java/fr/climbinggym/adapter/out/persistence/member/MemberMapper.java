package fr.climbinggym.adapter.out.persistence.member;

import fr.climbinggym.domain.member.Member;

final class MemberMapper {

    private MemberMapper() {
    }

    static MemberEntity toEntity(Member member) {
        return new MemberEntity(
                member.id(),
                member.firstName(),
                member.lastName(),
                member.email(),
                member.birthDate(),
                member.belayCertified(),
                member.createdAt()
        );
    }

    static Member toDomain(MemberEntity entity) {
        return Member.restore(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.isBelayCertified(),
                entity.getCreatedAt()
        );
    }
}
