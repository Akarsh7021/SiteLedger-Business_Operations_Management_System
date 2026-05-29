package com.familybusiness.payroll.contractor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class WorkSiteForm {

    private Long id;

    private Long contractorId;

    @NotBlank(message = "Work site location is required")
    @Size(max = 255, message = "Work site location must be 255 characters or less")
    private String location;

    @NotNull(message = "Quoted amount is required")
    @DecimalMin(value = "0.00", message = "Quoted amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Quoted amount must use dollars and cents")
    private BigDecimal quotedAmount = BigDecimal.ZERO;

    @NotNull(message = "Square area is required")
    @DecimalMin(value = "0.00", message = "Square area cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Square area must use up to 2 decimals")
    private BigDecimal squareArea = BigDecimal.ZERO;

    @NotNull(message = "Unit of measurement is required")
    private UnitOfMeasurement unitOfMeasurement = UnitOfMeasurement.SFT;

    private BigDecimal gstAmount = BigDecimal.ZERO;

    private WorkSiteStatus status = WorkSiteStatus.IN_PROGRESS;

    public static WorkSiteForm fromWorkSite(WorkSite workSite) {
        WorkSiteForm form = new WorkSiteForm();
        form.setId(workSite.getId());
        form.setContractorId(workSite.getContractor().getId());
        form.setLocation(workSite.getLocation());
        form.setQuotedAmount(workSite.getQuotedAmount());
        form.setSquareArea(workSite.getSquareArea());
        form.setUnitOfMeasurement(workSite.getUnitOfMeasurement());
        form.setGstAmount(workSite.getGstAmount());
        form.setStatus(workSite.getStatus());
        return form;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractorId() {
        return contractorId;
    }

    public void setContractorId(Long contractorId) {
        this.contractorId = contractorId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getQuotedAmount() {
        return quotedAmount;
    }

    public void setQuotedAmount(BigDecimal quotedAmount) {
        this.quotedAmount = quotedAmount;
    }

    public BigDecimal getSquareArea() {
        return squareArea;
    }

    public void setSquareArea(BigDecimal squareArea) {
        this.squareArea = squareArea;
    }

    public UnitOfMeasurement getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public WorkSiteStatus getStatus() {
        return status;
    }

    public void setStatus(WorkSiteStatus status) {
        this.status = status;
    }
}
