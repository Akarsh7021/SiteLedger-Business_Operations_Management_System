package com.familybusiness.payroll.employee;

import com.familybusiness.payroll.deletehistory.DeleteHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DeleteHistoryService deleteHistoryService;

    public EmployeeService(EmployeeRepository employeeRepository, DeleteHistoryService deleteHistoryService) {
        this.employeeRepository = employeeRepository;
        this.deleteHistoryService = deleteHistoryService;
    }

    @Transactional(readOnly = true)
    public List<Employee> findEmployees(String search) {
        if (search == null || search.isBlank()) {
            return employeeRepository.findAllByOrderByFullNameAsc();
        }
        return employeeRepository.findByFullNameContainingIgnoreCaseOrderByFullNameAsc(search.trim());
    }

    @Transactional(readOnly = true)
    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee createEmployee(EmployeeForm form) {
        Employee employee = new Employee();
        copyFormToEmployee(form, employee);
        return employeeRepository.save(employee);
    }

    public Employee createDefaultEmployee(String name) {
        Employee employee = new Employee();
        employee.setFullName(requireName(name));
        employee.setHourlyWage(new BigDecimal("0.01"));
        employee.setPosition("Worker");
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, EmployeeForm form) {
        Employee employee = getEmployee(id);
        copyFormToEmployee(form, employee);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployee(id);
        deleteHistoryService.record(
                "Employee",
                employee.getFullName(),
                "Position: " + employee.getPosition() + ", status: " + employee.getEmploymentStatus()
        );
        employeeRepository.delete(employee);
    }

    private void copyFormToEmployee(EmployeeForm form, Employee employee) {
        employee.setFullName(form.getFullName().trim());
        employee.setPhoneNumber(cleanOptionalText(form.getPhoneNumber()));
        employee.setAddress(cleanOptionalText(form.getAddress()));
        employee.setHourlyWage(form.getHourlyWage());
        employee.setPosition(form.getPosition().trim());
        employee.setEmploymentStatus(form.getEmploymentStatus());
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String requireName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        return value.trim();
    }
}
