package com.mes.mes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processes")
public class MesProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_id")
    private Long processId;

    @Column(name = "process_name", nullable = false, length = 100)
    private String processName;

    @Column(name = "process_order", nullable = false)
    private Integer processOrder;

    @Column(name = "standard_uph", nullable = false)
    private Integer standardUph;

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Integer getProcessOrder() {
        return processOrder;
    }

    public void setProcessOrder(Integer processOrder) {
        this.processOrder = processOrder;
    }

    public Integer getStandardUph() {
        return standardUph;
    }

    public void setStandardUph(Integer standardUph) {
        this.standardUph = standardUph;
    }
}
