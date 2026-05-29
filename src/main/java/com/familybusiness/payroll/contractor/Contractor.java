package com.familybusiness.payroll.contractor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contractors")
public class Contractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerType customerType = CustomerType.OWNER;

    @Column(length = 120)
    private String billingName;

    @Column(length = 255)
    private String address;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaidToDate = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountUnpaid = BigDecimal.ZERO;

    @OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkSite> workSites = new ArrayList<>();

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
        if (customerType == null) {
            return CustomerType.OWNER;
        }
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

    public List<WorkSite> getWorkSites() {
        return workSites;
    }

    public void setWorkSites(List<WorkSite> workSites) {
        this.workSites = workSites;
    }
}
