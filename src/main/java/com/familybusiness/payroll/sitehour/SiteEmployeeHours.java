package com.familybusiness.payroll.sitehour;

import java.math.BigDecimal;

public class SiteEmployeeHours {

    private final String employeeName;
    private final BigDecimal hours;

    public SiteEmployeeHours(String employeeName, BigDecimal hours) {
        this.employeeName = employeeName;
        this.hours = hours;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public BigDecimal getHours() {
        return hours;
    }
}
