package com.familybusiness.payroll.workhour;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkHourRepository extends JpaRepository<WorkHour, Long> {

    @EntityGraph(attributePaths = "employee")
    List<WorkHour> findAllByOrderByWorkDateDescEmployeeFullNameAsc();

    @EntityGraph(attributePaths = "employee")
    List<WorkHour> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);

    @EntityGraph(attributePaths = "employee")
    List<WorkHour> findByPaymentStatusOrderByWorkDateDescEmployeeFullNameAsc(PaymentStatus paymentStatus);

    @EntityGraph(attributePaths = "employee")
    List<WorkHour> findByEmployeeIdAndPaymentStatusOrderByWorkDateDesc(Long employeeId, PaymentStatus paymentStatus);
}
