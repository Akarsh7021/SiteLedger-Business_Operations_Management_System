package com.familybusiness.payroll.workhour;

public class WorkHourNotFoundException extends RuntimeException {

    public WorkHourNotFoundException(Long id) {
        super("Work hour entry not found: " + id);
    }
}
