package com.familybusiness.payroll.quickcreate;

import com.familybusiness.payroll.contractor.Contractor;
import com.familybusiness.payroll.contractor.ContractorForm;
import com.familybusiness.payroll.contractor.ContractorService;
import com.familybusiness.payroll.contractor.CustomerType;
import com.familybusiness.payroll.contractor.WorkSite;
import com.familybusiness.payroll.employee.Employee;
import com.familybusiness.payroll.employee.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/quick-create")
public class QuickCreateController {

    private final ContractorService contractorService;
    private final EmployeeService employeeService;

    public QuickCreateController(ContractorService contractorService, EmployeeService employeeService) {
        this.contractorService = contractorService;
        this.employeeService = employeeService;
    }

    @PostMapping("/customers")
    public ResponseEntity<QuickCreateOption> customer(@RequestBody Map<String, String> body) {
        Contractor contractor;
        if (body.size() > 1) {
            ContractorForm form = new ContractorForm();
            form.setName(body.get("name"));
            form.setPhoneNumber(body.get("phoneNumber"));
            form.setCustomerType(parseCustomerType(body.get("customerType")));
            form.setBillingName(body.get("billingName"));
            form.setAddress(body.get("address"));
            form.setAmountPaidToDate(parseMoney(body.get("amountPaidToDate")));
            form.setAmountUnpaid(parseMoney(body.get("amountUnpaid")));
            contractor = contractorService.createContractor(form);
        } else {
            contractor = contractorService.createDefaultContractor(body.get("name"));
        }
        return ResponseEntity.ok(new QuickCreateOption(contractor.getId(), contractor.getName()));
    }

    @PostMapping("/employees")
    public ResponseEntity<QuickCreateOption> employee(@RequestBody Map<String, String> body) {
        Employee employee = employeeService.createDefaultEmployee(body.get("name"));
        return ResponseEntity.ok(new QuickCreateOption(employee.getId(), employee.getFullName()));
    }

    @PostMapping("/work-sites")
    public ResponseEntity<QuickCreateOption> workSite(@RequestBody Map<String, String> body) {
        Long contractorId = body.get("contractorId") == null || body.get("contractorId").isBlank()
                ? null
                : Long.valueOf(body.get("contractorId"));
        WorkSite workSite = contractorService.createDefaultWorkSite(contractorId, body.get("name"));
        return ResponseEntity.ok(new QuickCreateOption(workSite.getId(), workSite.getLocation()));
    }

    public record QuickCreateOption(Long id, String name) {
    }

    private CustomerType parseCustomerType(String value) {
        if (value == null || value.isBlank()) {
            return CustomerType.BUILDER;
        }
        return CustomerType.valueOf(value);
    }

    private java.math.BigDecimal parseMoney(String value) {
        if (value == null || value.isBlank()) {
            return java.math.BigDecimal.ZERO;
        }
        return new java.math.BigDecimal(value);
    }
}
