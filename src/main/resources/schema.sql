-- MES 포트폴리오용 스키마 (Spring Boot JPA ddl-auto=none 기준 정합)
-- 실행: mysql -u root -p < src/main/resources/schema.sql

CREATE DATABASE IF NOT EXISTS mes_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mes_db;

-- ---------------------------------------------------------------------------
-- 1. users
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN', 'MANAGER', 'OPERATOR', 'QC', 'VIEWER') NOT NULL,
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 2. products
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    product_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50)  NOT NULL,
    product_name VARCHAR(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 3. processes
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS processes (
    process_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_name  VARCHAR(100) NOT NULL,
    process_order INT NOT NULL,
    standard_uph  INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 4. work_orders
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS work_orders (
    wo_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    wo_number    VARCHAR(50) NOT NULL UNIQUE,
    product_id   BIGINT NOT NULL,
    process_id   BIGINT NOT NULL,
    planned_qty  INT NOT NULL,
    status       ENUM('OPEN', 'IN_PROGRESS', 'DONE', 'ON_HOLD') NOT NULL,
    planned_date DATE NOT NULL,
    created_by   BIGINT NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wo_product  FOREIGN KEY (product_id) REFERENCES products (product_id),
    CONSTRAINT fk_wo_process  FOREIGN KEY (process_id) REFERENCES processes (process_id),
    CONSTRAINT fk_wo_user     FOREIGN KEY (created_by) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 5. lots
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS lots (
    lot_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_number VARCHAR(50) NOT NULL UNIQUE,
    wo_id      BIGINT NOT NULL,
    status     ENUM('WAITING', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD') NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lot_wo FOREIGN KEY (wo_id) REFERENCES work_orders (wo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 6. lot_history
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS lot_history (
    history_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_id      BIGINT NOT NULL,
    from_status VARCHAR(50),
    to_status   VARCHAR(50),
    changed_by  BIGINT NOT NULL,
    changed_at  DATETIME NOT NULL,
    remark      VARCHAR(500),
    CONSTRAINT fk_lh_lot FOREIGN KEY (lot_id) REFERENCES lots (lot_id),
    CONSTRAINT fk_lh_user FOREIGN KEY (changed_by) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 7. production_results
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS production_results (
    result_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_id      BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    good_qty    INT NOT NULL,
    defect_qty  INT NOT NULL,
    start_time  DATETIME NULL,
    end_time    DATETIME NULL,
    input_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pr_lot FOREIGN KEY (lot_id) REFERENCES lots (lot_id),
    CONSTRAINT fk_pr_user FOREIGN KEY (operator_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 8. defects
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS defects (
    defect_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_id   BIGINT NOT NULL,
    defect_type ENUM('EQUIPMENT', 'MATERIAL', 'OPERATOR', 'UNKNOWN') NOT NULL,
    defect_qty  INT NOT NULL,
    action      ENUM('REWORK', 'SCRAP', 'HOLD') NOT NULL,
    handled_by  BIGINT NULL,
    handled_at  DATETIME NULL,
    note        VARCHAR(500),
    CONSTRAINT fk_def_result FOREIGN KEY (result_id) REFERENCES production_results (result_id),
    CONSTRAINT fk_def_user   FOREIGN KEY (handled_by) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 샘플 데이터
-- ---------------------------------------------------------------------------
INSERT INTO users (username, password, role, is_active, created_at) VALUES
    ('admin',     'admin123', 'ADMIN',    1, NOW()),
    ('operator1', 'op123',    'OPERATOR', 1, NOW()),
    ('qc1',       'qc123',    'QC',       1, NOW());

INSERT INTO products (product_code, product_name) VALUES
    ('P-NAND128', 'NAND Flash 128GB'),
    ('P-DRAM8',   'DRAM 8GB');

INSERT INTO processes (process_name, process_order, standard_uph) VALUES
    ('Die Attach',     1, 1200),
    ('Wire Bonding',   2,  900),
    ('Molding',        3,  800);
