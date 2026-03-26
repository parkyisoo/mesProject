package com.mes.mes.repository;

import com.mes.mes.entity.Defect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DefectRepository extends JpaRepository<Defect, Long> {

    @Query("SELECT DISTINCT d FROM Defect d "
            + "JOIN FETCH d.productionResult pr "
            + "JOIN FETCH pr.lot l "
            + "JOIN FETCH l.workOrder w "
            + "JOIN FETCH w.product "
            + "LEFT JOIN FETCH d.handledBy "
            + "ORDER BY d.defectId DESC")
    List<Defect> findAllForList();
}
