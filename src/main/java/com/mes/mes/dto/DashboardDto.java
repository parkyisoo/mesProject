package com.mes.mes.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 대시보드 요약 데이터.
 */
public class DashboardDto {

    private long workOrdersOpenToday;
    private long workOrdersInProgressToday;
    private long workOrdersDoneToday;
    private long workOrdersOnHoldToday;

    private long lotsWaiting;
    private long lotsInProgress;
    private long lotsCompleted;
    private long lotsOnHold;

    private BigDecimal todayYieldPercent = BigDecimal.ZERO;

    private final List<LotDefectWarningRow> defectWarningLots = new ArrayList<>();

    public long getWorkOrdersOpenToday() {
        return workOrdersOpenToday;
    }

    public void setWorkOrdersOpenToday(long workOrdersOpenToday) {
        this.workOrdersOpenToday = workOrdersOpenToday;
    }

    public long getWorkOrdersInProgressToday() {
        return workOrdersInProgressToday;
    }

    public void setWorkOrdersInProgressToday(long workOrdersInProgressToday) {
        this.workOrdersInProgressToday = workOrdersInProgressToday;
    }

    public long getWorkOrdersDoneToday() {
        return workOrdersDoneToday;
    }

    public void setWorkOrdersDoneToday(long workOrdersDoneToday) {
        this.workOrdersDoneToday = workOrdersDoneToday;
    }

    public long getWorkOrdersOnHoldToday() {
        return workOrdersOnHoldToday;
    }

    public void setWorkOrdersOnHoldToday(long workOrdersOnHoldToday) {
        this.workOrdersOnHoldToday = workOrdersOnHoldToday;
    }

    public long getLotsWaiting() {
        return lotsWaiting;
    }

    public void setLotsWaiting(long lotsWaiting) {
        this.lotsWaiting = lotsWaiting;
    }

    public long getLotsInProgress() {
        return lotsInProgress;
    }

    public void setLotsInProgress(long lotsInProgress) {
        this.lotsInProgress = lotsInProgress;
    }

    public long getLotsCompleted() {
        return lotsCompleted;
    }

    public void setLotsCompleted(long lotsCompleted) {
        this.lotsCompleted = lotsCompleted;
    }

    public long getLotsOnHold() {
        return lotsOnHold;
    }

    public void setLotsOnHold(long lotsOnHold) {
        this.lotsOnHold = lotsOnHold;
    }

    public BigDecimal getTodayYieldPercent() {
        return todayYieldPercent;
    }

    public void setTodayYieldPercent(BigDecimal todayYieldPercent) {
        this.todayYieldPercent = todayYieldPercent;
    }

    public List<LotDefectWarningRow> getDefectWarningLots() {
        return Collections.unmodifiableList(defectWarningLots);
    }

    public void addDefectWarning(LotDefectWarningRow row) {
        defectWarningLots.add(row);
    }

    public static class LotDefectWarningRow {
        private String lotNumber;
        private int goodQty;
        private int defectQty;
        private BigDecimal defectRatePercent = BigDecimal.ZERO;

        public String getLotNumber() {
            return lotNumber;
        }

        public void setLotNumber(String lotNumber) {
            this.lotNumber = lotNumber;
        }

        public int getGoodQty() {
            return goodQty;
        }

        public void setGoodQty(int goodQty) {
            this.goodQty = goodQty;
        }

        public int getDefectQty() {
            return defectQty;
        }

        public void setDefectQty(int defectQty) {
            this.defectQty = defectQty;
        }

        public BigDecimal getDefectRatePercent() {
            return defectRatePercent;
        }

        public void setDefectRatePercent(BigDecimal defectRatePercent) {
            this.defectRatePercent = defectRatePercent;
        }
    }
}
