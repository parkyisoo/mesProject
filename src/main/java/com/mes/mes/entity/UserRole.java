package com.mes.mes.entity;

/**
 * Maps to MySQL {@code users.role} ENUM.
 * Align enum constant names with your database definition.
 */
public enum UserRole {
    ADMIN,
    MANAGER,
    OPERATOR,
    QC,
    VIEWER
}
