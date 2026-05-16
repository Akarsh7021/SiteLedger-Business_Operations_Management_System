package com.familybusiness.payroll.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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

    public Employee updateEmployee(Long id, EmployeeForm form) {
        Employee employee = getEmployee(id);
        copyFormToEmployee(form, employee);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        employeeRepository.deleteById(id);
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
}
