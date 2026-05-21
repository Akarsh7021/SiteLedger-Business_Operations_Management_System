CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    phone_number VARCHAR(30),
    address VARCHAR(255),
    hourly_wage NUMERIC(10, 2) NOT NULL,
    position VARCHAR(80) NOT NULL,
    employment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (employment_status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE IF NOT EXISTS work_hours (
    id INTEGER PRIMARY KEY,
    employee_id INTEGER NOT NULL,
    work_date DATE NOT NULL,
    regular_hours NUMERIC(5, 2) NOT NULL DEFAULT 0,
    overtime_hours NUMERIC(5, 2) NOT NULL DEFAULT 0,
    notes VARCHAR(500),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID'
        CHECK (payment_status IN ('UNPAID', 'PARTIAL', 'PAID')),
    payment_method VARCHAR(20)
        CHECK (payment_method IS NULL OR payment_method IN ('CASH', 'E_PAYMENT')),
    cash_payment_type VARCHAR(20)
        CHECK (cash_payment_type IS NULL OR cash_payment_type IN ('FULL', 'PARTIAL')),
    partial_payment_amount NUMERIC(10, 2) DEFAULT 0,
    paid_at TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_employees_full_name
    ON employees(full_name);

CREATE INDEX IF NOT EXISTS idx_work_hours_employee_id
    ON work_hours(employee_id);

CREATE INDEX IF NOT EXISTS idx_work_hours_work_date
    ON work_hours(work_date);

CREATE INDEX IF NOT EXISTS idx_work_hours_payment_status
    ON work_hours(payment_status);

CREATE TABLE IF NOT EXISTS contractors (
    id INTEGER PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    phone_number VARCHAR(30),
    amount_paid_to_date NUMERIC(10, 2) NOT NULL DEFAULT 0,
    amount_unpaid NUMERIC(10, 2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS contractor_work_sites (
    id INTEGER PRIMARY KEY,
    contractor_id INTEGER NOT NULL,
    location VARCHAR(255) NOT NULL,
    quoted_amount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    square_area NUMERIC(10, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (contractor_id) REFERENCES contractors(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_contractors_name
    ON contractors(name);

CREATE INDEX IF NOT EXISTS idx_contractor_work_sites_contractor_id
    ON contractor_work_sites(contractor_id);
