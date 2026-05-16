package com.familybusiness.payroll.employee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class EmployeeForm {

    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be 120 characters or less")
    private String fullName;

    @Size(max = 30, message = "Phone number must be 30 characters or less")
    private String phoneNumber;

    @Size(max = 255, message = "Address must be 255 characters or less")
    private String address;

    @NotNull(message = "Hourly wage is required")
    @DecimalMin(value = "0.01", message = "Hourly wage must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Hourly wage must use dollars and cents")
    private BigDecimal hourlyWage;

    @NotBlank(message = "Position is required")
    @Size(max = 80, message = "Position must be 80 characters or less")
    private String position;

    @NotNull(message = "Employment status is required")
    private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;

    public static EmployeeForm fromEmployee(Employee employee) {
        EmployeeForm form = new EmployeeForm();
        form.setId(employee.getId());
        form.setFullName(employee.getFullName());
        form.setPhoneNumber(employee.getPhoneNumber());
        form.setAddress(employee.getAddress());
        form.setHourlyWage(employee.getHourlyWage());
        form.setPosition(employee.getPosition());
        form.setEmploymentStatus(employee.getEmploymentStatus());
        return form;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(BigDecimal hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
}
