package com.familybusiness.payroll.contractor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contractor_work_sites")
public class WorkSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contractor_id", nullable = false)
    private Contractor contractor;

    @Column(nullable = false, length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ServiceType serviceType = ServiceType.DEEP_FULL_SERVICE_CLEANUP;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quotedAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal squareArea = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnitOfMeasurement unitOfMeasurement = UnitOfMeasurement.SFT;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkSiteStatus status = WorkSiteStatus.IN_PROGRESS;

    @Column(unique = true)
    private Integer invoiceNumber;

    private LocalDate invoiceDate;

    @Column(length = 1000)
    private String invoiceBillingAddress;

    @OneToMany(mappedBy = "workSite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> invoiceItems = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ServiceType getServiceType() {
        if (serviceType == null) {
            return ServiceType.DEEP_FULL_SERVICE_CLEANUP;
        }
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
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
        if (unitOfMeasurement == null) {
            return UnitOfMeasurement.SFT;
        }
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public BigDecimal getGstAmount() {
        if (gstAmount == null) {
            return BigDecimal.ZERO;
        }
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public WorkSiteStatus getStatus() {
        if (status == null) {
            return WorkSiteStatus.IN_PROGRESS;
        }
        return status;
    }

    public void setStatus(WorkSiteStatus status) {
        this.status = status;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceBillingAddress() {
        return invoiceBillingAddress;
    }

    public void setInvoiceBillingAddress(String invoiceBillingAddress) {
        this.invoiceBillingAddress = invoiceBillingAddress;
    }

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }
}
