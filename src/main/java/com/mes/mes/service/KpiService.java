package com.mes.mes.service;

import com.mes.mes.dto.KpiDto;
import com.mes.mes.entity.LotStatus;
import com.mes.mes.repository.LotRepository;
import com.mes.mes.repository.ProductionResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KpiService {

    private final ProductionResultRepository productionResultRepository;
    private final LotRepository lotRepository;

    public KpiService(ProductionResultRepository productionResultRepository, LotRepository lotRepository) {
        this.productionResultRepository = productionResultRepository;
        this.lotRepository = lotRepository;
    }

    @Transactional(readOnly = true)
    public KpiDto buildKpi(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now();
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        if (end.isBefore(start)) {
            LocalDate t = start;
            start = end;
            end = t;
        }

        LocalDateTime rangeStart = start.atStartOfDay();
        LocalDateTime rangeEnd = end.plusDays(1).atStartOfDay();

        KpiDto dto = new KpiDto();
        dto.setRangeStart(start);
        dto.setRangeEnd(end);

        long sumGood = 0L;
        long sumDefect = 0L;
        List<Object[]> totals = productionResultRepository.sumGoodAndDefectInRange(rangeStart, rangeEnd);
        if (!totals.isEmpty()) {
            Object[] row = totals.get(0);
            sumGood = toLong(row[0]);
            sumDefect = toLong(row[1]);
        }
        long sumTotal = sumGood + sumDefect;
        dto.setYieldPercent(percent(sumGood, sumTotal, 2));
        dto.setDefectRatePercent(percent(sumDefect, sumTotal, 2));

        long workSeconds = 0L;
        List<Object[]> secRows = productionResultRepository.sumWorkSecondsInRange(rangeStart, rangeEnd);
        if (!secRows.isEmpty()) {
            workSeconds = toLong(secRows.get(0)[0]);
        }
        if (workSeconds > 0) {
            BigDecimal hours = BigDecimal.valueOf(workSeconds).divide(BigDecimal.valueOf(3600), 6, RoundingMode.HALF_UP);
            dto.setUph(BigDecimal.valueOf(sumGood).divide(hours, 2, RoundingMode.HALF_UP));
        } else {
            dto.setUph(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        long totalLots = lotRepository.countCreatedBetween(rangeStart, rangeEnd);
        long completedLots = lotRepository.countCreatedBetweenAndStatus(rangeStart, rangeEnd, LotStatus.COMPLETED);
        dto.setLotCompletionRatePercent(percent(completedLots, totalLots, 2));

        for (Object[] dayRow : productionResultRepository.sumByDayInRange(rangeStart, rangeEnd)) {
            KpiDto.DailyKpiRow dr = new KpiDto.DailyKpiRow();
            dr.setDate(toLocalDate(dayRow[0]));
            long g = toLong(dayRow[1]);
            long d = toLong(dayRow[2]);
            dr.setGoodQty(g);
            dr.setDefectQty(d);
            dr.setYieldPercent(percent(g, g + d, 2));
            dto.addDailyRow(dr);
        }

        for (Object[] pr : productionResultRepository.sumYieldByProcessInRange(rangeStart, rangeEnd)) {
            String name = pr[0] != null ? pr[0].toString() : "-";
            long g = toLong(pr[1]);
            long t = toLong(pr[2]);
            dto.addProcessChartPoint(name, percent(g, t, 2));
        }

        for (Object[] dr : productionResultRepository.sumDefectQtyByTypeInRange(rangeStart, rangeEnd)) {
            String type = dr[0] != null ? dr[0].toString() : "UNKNOWN";
            dto.addDefectChartSlice(type, toLong(dr[1]));
        }

        return dto;
    }

    private static BigDecimal percent(long numerator, long denominator, int scale) {
        if (denominator <= 0) {
            return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), scale, RoundingMode.HALF_UP);
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

    private static LocalDate toLocalDate(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof LocalDate) {
            return (LocalDate) o;
        }
        if (o instanceof java.sql.Date) {
            return ((java.sql.Date) o).toLocalDate();
        }
        if (o instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) o).getTime()).toLocalDate();
        }
        return LocalDate.parse(o.toString());
    }
}
