package com.familybusiness.payroll.workhour;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkHourRepository extends JpaRepository<WorkHour, Long> {

    @EntityGraph(attributePaths = {"employee", "workSite"})
    List<WorkHour> findAllByOrderByWorkDateDescEmployeeFullNameAsc();

    @EntityGraph(attributePaths = {"employee", "workSite"})
    List<WorkHour> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);

    @EntityGraph(attributePaths = {"employee", "workSite"})
    List<WorkHour> findByPaymentStatusOrderByWorkDateDescEmployeeFullNameAsc(PaymentStatus paymentStatus);

    @EntityGraph(attributePaths = {"employee", "workSite"})
    List<WorkHour> findByEmployeeIdAndPaymentStatusOrderByWorkDateDesc(Long employeeId, PaymentStatus paymentStatus);

    @EntityGraph(attributePaths = {"employee", "workSite"})
    @Query("select wh from WorkHour wh where wh.workSite is not null")
    List<WorkHour> findAllWithWorkSite();
}
