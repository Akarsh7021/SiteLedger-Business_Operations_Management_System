package com.familybusiness.payroll.sitehour;

import com.familybusiness.payroll.contractor.WorkSite;

import java.math.BigDecimal;
import java.util.List;

public class SiteHourSummary {

    private final WorkSite workSite;
    private final BigDecimal accumulatedHours;
    private final BigDecimal totalSpent;
    private final List<SiteEmployeeHours> employeeHours;

    public SiteHourSummary(
            WorkSite workSite,
            BigDecimal accumulatedHours,
            BigDecimal totalSpent,
            List<SiteEmployeeHours> employeeHours
    ) {
        this.workSite = workSite;
        this.accumulatedHours = accumulatedHours;
        this.totalSpent = totalSpent;
        this.employeeHours = employeeHours;
    }

    public WorkSite getWorkSite() {
        return workSite;
    }

    public BigDecimal getAccumulatedHours() {
        return accumulatedHours;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public List<SiteEmployeeHours> getEmployeeHours() {
        return employeeHours;
    }
}
