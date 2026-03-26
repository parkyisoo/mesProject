package com.mes.mes.repository;

import com.mes.mes.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    Optional<WorkOrder> findFirstByWoNumberStartingWithOrderByWoNumberDesc(String prefix);

    @Query("SELECT DISTINCT w FROM WorkOrder w JOIN FETCH w.product JOIN FETCH w.process ORDER BY w.createdAt DESC")
    List<WorkOrder> findAllWithProductAndProcess();
}
