package com.familybusiness.payroll.workhour;

import com.familybusiness.payroll.employee.Employee;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_hours")
public class WorkHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate workDate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal regularHours = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CashPaymentType cashPaymentType;

    @Column(precision = 10, scale = 2)
    private BigDecimal partialPaymentAmount = BigDecimal.ZERO;

    private LocalDateTime paidAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public PaymentStatus getPaymentStatus() {
        if (paymentStatus == null) {
            return PaymentStatus.UNPAID;
        }
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public CashPaymentType getCashPaymentType() {
        return cashPaymentType;
    }

    public void setCashPaymentType(CashPaymentType cashPaymentType) {
        this.cashPaymentType = cashPaymentType;
    }

    public BigDecimal getPartialPaymentAmount() {
        if (partialPaymentAmount == null) {
            return BigDecimal.ZERO;
        }
        return partialPaymentAmount;
    }

    public void setPartialPaymentAmount(BigDecimal partialPaymentAmount) {
        this.partialPaymentAmount = partialPaymentAmount;
    }

    public BigDecimal getTotalHours() {
        return safeAmount(regularHours).add(safeAmount(overtimeHours));
    }

    public BigDecimal getTotalPaymentAmount() {
        if (employee == null || employee.getHourlyWage() == null) {
            return BigDecimal.ZERO;
        }
        return getTotalHours().multiply(employee.getHourlyWage());
    }

    public BigDecimal getRemainingPaymentAmount() {
        BigDecimal remaining = getTotalPaymentAmount().subtract(getPartialPaymentAmount());
        if (remaining.signum() < 0) {
            return BigDecimal.ZERO;
        }
        return remaining;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    @PrePersist
    @PreUpdate
    private void applyDefaults() {
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.UNPAID;
        }
        if (partialPaymentAmount == null) {
            partialPaymentAmount = BigDecimal.ZERO;
        }
    }

    private BigDecimal safeAmount(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value;
    }
}
