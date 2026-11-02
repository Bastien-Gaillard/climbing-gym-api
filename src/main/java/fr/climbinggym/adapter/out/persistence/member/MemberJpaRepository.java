package fr.climbinggym.adapter.out.persistence.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface MemberJpaRepository extends JpaRepository<MemberEntity, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    List<MemberEntity> findAllByOrderByLastNameAscFirstNameAsc();

    @Query("""
            select m from MemberEntity m
            where lower(m.lastName) like lower(concat('%', :query, '%'))
               or lower(m.firstName) like lower(concat('%', :query, '%'))
               or lower(m.email) like lower(concat('%', :query, '%'))
            order by m.lastName, m.firstName
            """)
    List<MemberEntity> search(@Param("query") String query);
}
