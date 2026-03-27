package com.mes.mes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * KPI 조회 화면용 집계 결과.
 */
public class KpiDto {

    private BigDecimal yieldPercent = BigDecimal.ZERO;
    private BigDecimal defectRatePercent = BigDecimal.ZERO;
    private BigDecimal uph = BigDecimal.ZERO;
    private BigDecimal lotCompletionRatePercent = BigDecimal.ZERO;

    private LocalDate rangeStart;
    private LocalDate rangeEnd;

    private final List<DailyKpiRow> dailyRows = new ArrayList<>();
    private final List<String> processChartLabels = new ArrayList<>();
    private final List<BigDecimal> processChartYields = new ArrayList<>();
    private final List<String> defectChartLabels = new ArrayList<>();
    private final List<Long> defectChartQtys = new ArrayList<>();

    public BigDecimal getYieldPercent() {
        return yieldPercent;
    }

    public void setYieldPercent(BigDecimal yieldPercent) {
        this.yieldPercent = yieldPercent;
    }

    public BigDecimal getDefectRatePercent() {
        return defectRatePercent;
    }

    public void setDefectRatePercent(BigDecimal defectRatePercent) {
        this.defectRatePercent = defectRatePercent;
    }

    public BigDecimal getUph() {
        return uph;
    }

    public void setUph(BigDecimal uph) {
        this.uph = uph;
    }

    public BigDecimal getLotCompletionRatePercent() {
        return lotCompletionRatePercent;
    }

    public void setLotCompletionRatePercent(BigDecimal lotCompletionRatePercent) {
        this.lotCompletionRatePercent = lotCompletionRatePercent;
    }

    public LocalDate getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(LocalDate rangeStart) {
        this.rangeStart = rangeStart;
    }

    public LocalDate getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(LocalDate rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public List<DailyKpiRow> getDailyRows() {
        return Collections.unmodifiableList(dailyRows);
    }

    public void addDailyRow(DailyKpiRow row) {
        dailyRows.add(row);
    }

    public List<String> getProcessChartLabels() {
        return Collections.unmodifiableList(processChartLabels);
    }

    public List<BigDecimal> getProcessChartYields() {
        return Collections.unmodifiableList(processChartYields);
    }

    public void addProcessChartPoint(String label, BigDecimal yieldPercent) {
        processChartLabels.add(label);
        processChartYields.add(yieldPercent);
    }

    public List<String> getDefectChartLabels() {
        return Collections.unmodifiableList(defectChartLabels);
    }

    public List<Long> getDefectChartQtys() {
        return Collections.unmodifiableList(defectChartQtys);
    }

    public void addDefectChartSlice(String label, long qty) {
        defectChartLabels.add(label);
        defectChartQtys.add(qty);
    }

    /**
     * 일별 양품/불량/수율 테이블 행.
     */
    public static class DailyKpiRow {
        private LocalDate date;
        private long goodQty;
        private long defectQty;
        private BigDecimal yieldPercent = BigDecimal.ZERO;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public long getGoodQty() {
            return goodQty;
        }

        public void setGoodQty(long goodQty) {
            this.goodQty = goodQty;
        }

        public long getDefectQty() {
            return defectQty;
        }

        public void setDefectQty(long defectQty) {
            this.defectQty = defectQty;
        }

        public BigDecimal getYieldPercent() {
            return yieldPercent;
        }

        public void setYieldPercent(BigDecimal yieldPercent) {
            this.yieldPercent = yieldPercent;
        }
    }
}
