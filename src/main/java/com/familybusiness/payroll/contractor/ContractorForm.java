package com.familybusiness.payroll.contractor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ContractorForm {

    private Long id;

    @NotBlank(message = "Contractor name is required")
    @Size(max = 120, message = "Contractor name must be 120 characters or less")
    private String name;

    @Size(max = 30, message = "Phone number must be 30 characters or less")
    private String phoneNumber;

    @NotNull(message = "Customer type is required")
    private CustomerType customerType = CustomerType.OWNER;

    @Size(max = 120, message = "Billing name must be 120 characters or less")
    private String billingName;

    @Size(max = 255, message = "Address must be 255 characters or less")
    private String address;

    @Size(max = 1000, message = "Notes must be 1000 characters or less")
    private String notes;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.00", message = "Amount paid cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Amount paid must use dollars and cents")
    private BigDecimal amountPaidToDate = BigDecimal.ZERO;

    @NotNull(message = "Amount unpaid is required")
    @DecimalMin(value = "0.00", message = "Amount unpaid cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Amount unpaid must use dollars and cents")
    private BigDecimal amountUnpaid = BigDecimal.ZERO;

    public static ContractorForm fromContractor(Contractor contractor) {
        ContractorForm form = new ContractorForm();
        form.setId(contractor.getId());
        form.setName(contractor.getName());
        form.setPhoneNumber(contractor.getPhoneNumber());
        form.setCustomerType(contractor.getCustomerType());
        form.setBillingName(contractor.getBillingName());
        form.setAddress(contractor.getAddress());
        form.setNotes(contractor.getNotes());
        form.setAmountPaidToDate(contractor.getAmountPaidToDate());
        form.setAmountUnpaid(contractor.getAmountUnpaid());
        return form;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getAmountPaidToDate() {
        return amountPaidToDate;
    }

    public void setAmountPaidToDate(BigDecimal amountPaidToDate) {
        this.amountPaidToDate = amountPaidToDate;
    }

    public BigDecimal getAmountUnpaid() {
        return amountUnpaid;
    }

    public void setAmountUnpaid(BigDecimal amountUnpaid) {
        this.amountUnpaid = amountUnpaid;
    }
}
