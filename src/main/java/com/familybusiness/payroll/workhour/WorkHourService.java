package com.familybusiness.payroll.workhour;

import com.familybusiness.payroll.employee.Employee;
import com.familybusiness.payroll.employee.EmployeeNotFoundException;
import com.familybusiness.payroll.employee.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WorkHourService {

    private final WorkHourRepository workHourRepository;
    private final EmployeeRepository employeeRepository;

    public WorkHourService(WorkHourRepository workHourRepository, EmployeeRepository employeeRepository) {
        this.workHourRepository = workHourRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkHour> findWorkHours(Long employeeId, PaymentStatus paymentStatus) {
        if (employeeId == null && paymentStatus == null) {
            return workHourRepository.findAllByOrderByWorkDateDescEmployeeFullNameAsc();
        }
        if (employeeId == null) {
            return workHourRepository.findByPaymentStatusOrderByWorkDateDescEmployeeFullNameAsc(paymentStatus);
        }
        if (paymentStatus == null) {
            return workHourRepository.findByEmployeeIdOrderByWorkDateDesc(employeeId);
        }
        return workHourRepository.findByEmployeeIdAndPaymentStatusOrderByWorkDateDesc(employeeId, paymentStatus);
    }

    @Transactional(readOnly = true)
    public List<WorkHour> findWorkHours(Long employeeId) {
        if (employeeId == null) {
            return workHourRepository.findAllByOrderByWorkDateDescEmployeeFullNameAsc();
        }
        return workHourRepository.findByEmployeeIdOrderByWorkDateDesc(employeeId);
    }

    @Transactional(readOnly = true)
    public WorkHour getWorkHour(Long id) {
        return workHourRepository.findById(id)
                .orElseThrow(() -> new WorkHourNotFoundException(id));
    }

    public WorkHour createWorkHour(WorkHourForm form) {
        WorkHour workHour = new WorkHour();
        copyFormToWorkHour(form, workHour);
        return workHourRepository.save(workHour);
    }

    public WorkHour updateWorkHour(Long id, WorkHourForm form) {
        WorkHour workHour = getWorkHour(id);
        if (workHour.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Paid work hours cannot be edited.");
        }
        copyFormToWorkHour(form, workHour);
        return workHourRepository.save(workHour);
    }

    public void markPaid(Long id, PaymentMethod paymentMethod, CashPaymentType cashPaymentType, BigDecimal partialAmount) {
        WorkHour workHour = getWorkHour(id);
        CashPaymentType resolvedCashPaymentType = paymentMethod == PaymentMethod.CASH
                ? cashPaymentType == null ? CashPaymentType.FULL : cashPaymentType
                : null;

        if (paymentMethod == PaymentMethod.CASH && resolvedCashPaymentType == CashPaymentType.PARTIAL) {
            BigDecimal cleanPartialAmount = validatePartialAmount(workHour, partialAmount);
            workHour.setPaymentStatus(PaymentStatus.PARTIAL);
            workHour.setPaymentMethod(paymentMethod);
            workHour.setCashPaymentType(CashPaymentType.PARTIAL);
            workHour.setPartialPaymentAmount(cleanPartialAmount);
            workHour.setPaidAt(null);
            workHourRepository.save(workHour);
            return;
        }

        workHour.setPaymentStatus(PaymentStatus.PAID);
        workHour.setPaymentMethod(paymentMethod);
        workHour.setCashPaymentType(resolvedCashPaymentType);
        workHour.setPartialPaymentAmount(workHour.getTotalPaymentAmount());
        workHour.setPaidAt(LocalDateTime.now());
        workHourRepository.save(workHour);
    }

    public void deleteWorkHour(Long id) {
        WorkHour workHour = getWorkHour(id);
        if (workHour.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Paid work hours cannot be deleted.");
        }
        workHourRepository.delete(workHour);
    }

    private BigDecimal validatePartialAmount(WorkHour workHour, BigDecimal partialAmount) {
        if (partialAmount == null || partialAmount.signum() <= 0) {
            throw new IllegalArgumentException("Partial payment amount must be greater than 0.");
        }
        if (partialAmount.compareTo(workHour.getTotalPaymentAmount()) >= 0) {
            throw new IllegalArgumentException("Partial payment amount must be less than the full payment amount.");
        }
        return partialAmount;
    }

    private void copyFormToWorkHour(WorkHourForm form, WorkHour workHour) {
        Employee employee = employeeRepository.findById(form.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(form.getEmployeeId()));

        workHour.setEmployee(employee);
        workHour.setWorkDate(form.getWorkDate());
        workHour.setRegularHours(form.getRegularHours());
        workHour.setOvertimeHours(form.getOvertimeHours());
        workHour.setNotes(cleanOptionalText(form.getNotes()));
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
