package com.mes.mes.repository;

import com.mes.mes.entity.ProductionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionResultRepository extends JpaRepository<ProductionResult, Long> {

    boolean existsByLot_LotId(Long lotId);
}
