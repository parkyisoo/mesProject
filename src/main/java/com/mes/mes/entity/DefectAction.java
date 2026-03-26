package com.mes.mes.entity;

/**
 * MySQL {@code defects.action}: {@code ENUM('REWORK','SCRAP','HOLD')}.
 * 신규/미처리 불량은 {@link #HOLD}로 두고, 처리 시 {@link #REWORK} 또는 {@link #SCRAP} 선택.
 */
public enum DefectAction {
    REWORK,
    SCRAP,
    HOLD
}
