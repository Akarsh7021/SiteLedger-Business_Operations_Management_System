package com.familybusiness.payroll.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByFullNameContainingIgnoreCaseOrderByFullNameAsc(String fullName);

    List<Employee> findAllByOrderByFullNameAsc();
}
