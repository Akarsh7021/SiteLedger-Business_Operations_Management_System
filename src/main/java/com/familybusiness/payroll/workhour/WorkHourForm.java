package com.familybusiness.payroll.workhour;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WorkHourForm {

    private Long id;

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Site location is required")
    private Long workSiteId;

    @NotNull(message = "Work date is required")
    private LocalDate workDate = LocalDate.now();

    @NotNull(message = "Regular hours are required")
    @DecimalMin(value = "0.00", message = "Regular hours cannot be negative")
    @DecimalMax(value = "24.00", message = "Regular hours cannot exceed 24")
    @Digits(integer = 2, fraction = 2, message = "Regular hours must use up to 2 decimals")
    private BigDecimal regularHours = BigDecimal.ZERO;

    @NotNull(message = "Overtime hours are required")
    @DecimalMin(value = "0.00", message = "Overtime hours cannot be negative")
    @DecimalMax(value = "24.00", message = "Overtime hours cannot exceed 24")
    @Digits(integer = 2, fraction = 2, message = "Overtime hours must use up to 2 decimals")
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Size(max = 500, message = "Notes must be 500 characters or less")
    private String notes;

    public static WorkHourForm fromWorkHour(WorkHour workHour) {
        WorkHourForm form = new WorkHourForm();
        form.setId(workHour.getId());
        form.setEmployeeId(workHour.getEmployee().getId());
        if (workHour.getWorkSite() != null) {
            form.setWorkSiteId(workHour.getWorkSite().getId());
        }
        form.setWorkDate(workHour.getWorkDate());
        form.setRegularHours(workHour.getRegularHours());
        form.setOvertimeHours(workHour.getOvertimeHours());
        form.setNotes(workHour.getNotes());
        return form;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getWorkSiteId() {
        return workSiteId;
    }

    public void setWorkSiteId(Long workSiteId) {
        this.workSiteId = workSiteId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public BigDecimal getRegularHours() {
        return regularHours;
    }

    public void setRegularHours(BigDecimal regularHours) {
        this.regularHours = regularHours;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
