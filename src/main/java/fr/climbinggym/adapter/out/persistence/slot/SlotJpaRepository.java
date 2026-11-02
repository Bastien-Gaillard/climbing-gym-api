package fr.climbinggym.adapter.out.persistence.slot;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SlotJpaRepository extends JpaRepository<SlotEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SlotEntity s where s.id = :id")
    Optional<SlotEntity> findByIdForUpdate(@Param("id") UUID id);

    List<SlotEntity> findByStartsAtGreaterThanEqualAndStartsAtLessThanOrderByStartsAtAsc(LocalDateTime from, LocalDateTime to);
}
