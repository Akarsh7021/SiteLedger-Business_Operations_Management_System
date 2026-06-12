package com.familybusiness.payroll.config;

import jakarta.annotation.PostConstruct;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SQLiteSchemaMigration {

    private final JdbcTemplate jdbcTemplate;

    public SQLiteSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void removeServiceTypeCheckConstraint() {
        String tableSql;
        try {
            tableSql = jdbcTemplate.queryForObject(
                    "select sql from sqlite_master where type = 'table' and name = 'contractor_work_sites'",
                    String.class
            );
        } catch (EmptyResultDataAccessException exception) {
            return;
        }
        if (tableSql == null || !tableSql.contains("service_type IN")) {
            return;
        }

        jdbcTemplate.execute("PRAGMA foreign_keys=OFF");
        jdbcTemplate.execute("""
                CREATE TABLE contractor_work_sites_migration (
                    id INTEGER PRIMARY KEY,
                    contractor_id INTEGER NOT NULL,
                    location VARCHAR(255) NOT NULL,
                    service_type VARCHAR(40) NOT NULL DEFAULT 'DEEP_FULL_SERVICE_CLEANUP',
                    quoted_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
                    square_area NUMERIC(10, 2) NOT NULL DEFAULT 0,
                    unit_of_measurement VARCHAR(20) NOT NULL DEFAULT 'SFT'
                        CHECK (unit_of_measurement IN ('LSM', 'SFT')),
                    gst_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
                    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS'
                        CHECK (status IN ('IN_PROGRESS', 'COMPLETE')),
                    invoice_number INTEGER UNIQUE,
                    invoice_date DATE,
                    invoice_billing_address VARCHAR(1000),
                    FOREIGN KEY (contractor_id) REFERENCES contractors(id) ON DELETE CASCADE
                )
                """);
        jdbcTemplate.execute("""
                INSERT INTO contractor_work_sites_migration (
                    id, contractor_id, location, service_type, quoted_amount, square_area,
                    unit_of_measurement, gst_amount, status, invoice_number, invoice_date,
                    invoice_billing_address
                )
                SELECT
                    id, contractor_id, location, service_type, quoted_amount, square_area,
                    unit_of_measurement, gst_amount, status, invoice_number, invoice_date,
                    invoice_billing_address
                FROM contractor_work_sites
                """);
        jdbcTemplate.execute("DROP TABLE contractor_work_sites");
        jdbcTemplate.execute("ALTER TABLE contractor_work_sites_migration RENAME TO contractor_work_sites");
        jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_contractor_work_sites_contractor_id
                    ON contractor_work_sites(contractor_id)
                """);
        jdbcTemplate.execute("PRAGMA foreign_keys=ON");
    }
}
