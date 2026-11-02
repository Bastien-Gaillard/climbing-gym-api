package fr.climbinggym.adapter.out.persistence.equipment;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface EquipmentJpaRepository extends JpaRepository<EquipmentEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EquipmentEntity e where e.id = :id")
    Optional<EquipmentEntity> findByIdForUpdate(@Param("id") UUID id);

    List<EquipmentEntity> findAllByOrderByCategoryAscNameAscSizeAsc();
}
