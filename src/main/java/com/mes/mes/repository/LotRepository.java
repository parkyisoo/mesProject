package com.mes.mes.repository;

import com.mes.mes.entity.Lot;
import com.mes.mes.entity.LotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long> {

    Optional<Lot> findFirstByLotNumberStartingWithOrderByLotNumberDesc(String prefix);

    @Query("SELECT DISTINCT l FROM Lot l JOIN FETCH l.workOrder w JOIN FETCH w.product JOIN FETCH w.process ORDER BY l.createdAt DESC")
    List<Lot> findAllWithDetails();

    @Query("SELECT l FROM Lot l JOIN FETCH l.workOrder w JOIN FETCH w.product WHERE l.lotId = :id")
    Optional<Lot> findByIdWithWorkOrderAndProduct(@Param("id") Long id);

    long countByStatus(LotStatus status);

    @Query("SELECT COUNT(l) FROM Lot l WHERE l.createdAt >= :start AND l.createdAt < :end")
    long countCreatedBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    @Query("SELECT COUNT(l) FROM Lot l WHERE l.createdAt >= :start AND l.createdAt < :end AND l.status = :st")
    long countCreatedBetweenAndStatus(
            @Param("start") java.time.LocalDateTime start,
            @Param("end") java.time.LocalDateTime end,
            @Param("st") LotStatus st);
}
