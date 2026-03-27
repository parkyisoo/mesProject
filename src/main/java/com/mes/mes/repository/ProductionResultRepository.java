package com.mes.mes.repository;

import com.mes.mes.entity.ProductionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductionResultRepository extends JpaRepository<ProductionResult, Long> {

    boolean existsByLot_LotId(Long lotId);

    @Query(value = "SELECT COALESCE(SUM(good_qty), 0), COALESCE(SUM(defect_qty), 0) FROM production_results "
            + "WHERE input_at >= :start AND input_at < :end", nativeQuery = true)
    List<Object[]> sumGoodAndDefectInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT COALESCE(SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)), 0) FROM production_results "
            + "WHERE input_at >= :start AND input_at < :end AND start_time IS NOT NULL AND end_time IS NOT NULL",
            nativeQuery = true)
    List<Object[]> sumWorkSecondsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT DATE(input_at) AS d, COALESCE(SUM(good_qty), 0), COALESCE(SUM(defect_qty), 0) "
            + "FROM production_results WHERE input_at >= :start AND input_at < :end "
            + "GROUP BY DATE(input_at) ORDER BY d", nativeQuery = true)
    List<Object[]> sumByDayInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT p.process_name, COALESCE(SUM(pr.good_qty), 0), COALESCE(SUM(pr.good_qty + pr.defect_qty), 0) "
            + "FROM production_results pr "
            + "INNER JOIN lots l ON pr.lot_id = l.lot_id "
            + "INNER JOIN work_orders w ON l.wo_id = w.wo_id "
            + "INNER JOIN processes p ON w.process_id = p.process_id "
            + "WHERE pr.input_at >= :start AND pr.input_at < :end "
            + "GROUP BY p.process_id, p.process_name ORDER BY p.process_name", nativeQuery = true)
    List<Object[]> sumYieldByProcessInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT d.defect_type, COALESCE(SUM(d.defect_qty), 0) FROM defects d "
            + "INNER JOIN production_results pr ON d.result_id = pr.result_id "
            + "WHERE pr.input_at >= :start AND pr.input_at < :end "
            + "GROUP BY d.defect_type ORDER BY d.defect_type", nativeQuery = true)
    List<Object[]> sumDefectQtyByTypeInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT COALESCE(SUM(good_qty), 0), COALESCE(SUM(defect_qty), 0) FROM production_results "
            + "WHERE DATE(input_at) = :d", nativeQuery = true)
    List<Object[]> sumGoodAndDefectOnDate(@Param("d") LocalDate d);

    @Query(value = "SELECT l.lot_number, pr.good_qty, pr.defect_qty FROM production_results pr "
            + "INNER JOIN lots l ON pr.lot_id = l.lot_id "
            + "WHERE DATE(pr.input_at) = :d "
            + "AND (pr.good_qty + pr.defect_qty) > 0 "
            + "AND (pr.defect_qty * 1.0 / (pr.good_qty + pr.defect_qty)) > 0.05", nativeQuery = true)
    List<Object[]> findLotsWithDefectRateOver5PercentOnDate(@Param("d") LocalDate d);
}
