package com.mes.mes.service;

import com.mes.mes.dto.DashboardDto;
import com.mes.mes.entity.LotStatus;
import com.mes.mes.entity.WorkOrderStatus;
import com.mes.mes.repository.LotRepository;
import com.mes.mes.repository.ProductionResultRepository;
import com.mes.mes.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final WorkOrderRepository workOrderRepository;
    private final LotRepository lotRepository;
    private final ProductionResultRepository productionResultRepository;

    public DashboardService(
            WorkOrderRepository workOrderRepository,
            LotRepository lotRepository,
            ProductionResultRepository productionResultRepository) {
        this.workOrderRepository = workOrderRepository;
        this.lotRepository = lotRepository;
        this.productionResultRepository = productionResultRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDto load() {
        LocalDate today = LocalDate.now();
        DashboardDto dto = new DashboardDto();

        dto.setWorkOrdersOpenToday(workOrderRepository.countByPlannedDateAndStatus(today, WorkOrderStatus.OPEN));
        dto.setWorkOrdersInProgressToday(
                workOrderRepository.countByPlannedDateAndStatus(today, WorkOrderStatus.IN_PROGRESS));
        dto.setWorkOrdersDoneToday(workOrderRepository.countByPlannedDateAndStatus(today, WorkOrderStatus.DONE));
        dto.setWorkOrdersOnHoldToday(workOrderRepository.countByPlannedDateAndStatus(today, WorkOrderStatus.ON_HOLD));

        dto.setLotsWaiting(lotRepository.countByStatus(LotStatus.WAITING));
        dto.setLotsInProgress(lotRepository.countByStatus(LotStatus.IN_PROGRESS));
        dto.setLotsCompleted(lotRepository.countByStatus(LotStatus.COMPLETED));
        dto.setLotsOnHold(lotRepository.countByStatus(LotStatus.ON_HOLD));

        List<Object[]> todayAgg = productionResultRepository.sumGoodAndDefectOnDate(today);
        long good = 0L;
        long defect = 0L;
        if (!todayAgg.isEmpty()) {
            Object[] row = todayAgg.get(0);
            good = toLong(row[0]);
            defect = toLong(row[1]);
        }
        long total = good + defect;
        if (total > 0) {
            dto.setTodayYieldPercent(BigDecimal.valueOf(good)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));
        } else {
            dto.setTodayYieldPercent(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        for (Object[] w : productionResultRepository.findLotsWithDefectRateOver5PercentOnDate(today)) {
            DashboardDto.LotDefectWarningRow r = new DashboardDto.LotDefectWarningRow();
            r.setLotNumber(w[0] != null ? w[0].toString() : "-");
            int g = (int) toLong(w[1]);
            int d = (int) toLong(w[2]);
            r.setGoodQty(g);
            r.setDefectQty(d);
            int sum = g + d;
            if (sum > 0) {
                r.setDefectRatePercent(BigDecimal.valueOf(d)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(sum), 2, RoundingMode.HALF_UP));
            }
            dto.addDefectWarning(r);
        }

        return dto;
    }

    private static long toLong(Object o) {
        if (o == null) {
            return 0L;
        }
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        return Long.parseLong(o.toString());
    }
}
