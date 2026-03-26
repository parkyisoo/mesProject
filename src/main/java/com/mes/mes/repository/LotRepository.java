package com.mes.mes.repository;

import com.mes.mes.entity.Lot;
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
}
