package com.familybusiness.payroll.employee;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String listEmployees(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("employees", employeeService.findEmployees(search));
        model.addAttribute("search", search);
        return "employees/list";
    }

    @GetMapping("/new")
    public String newEmployee(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());
        model.addAttribute("statuses", EmploymentStatus.values());
        model.addAttribute("pageTitle", "Add Employee");
        return "employees/form";
    }

    @PostMapping
    public String createEmployee(
            @Valid @ModelAttribute EmployeeForm employeeForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", EmploymentStatus.values());
            model.addAttribute("pageTitle", "Add Employee");
            return "employees/form";
        }

        employeeService.createEmployee(employeeForm);
        redirectAttributes.addFlashAttribute("message", "Employee added.");
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String editEmployee(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployee(id);
        model.addAttribute("employeeForm", EmployeeForm.fromEmployee(employee));
        model.addAttribute("statuses", EmploymentStatus.values());
        model.addAttribute("pageTitle", "Edit Employee");
        return "employees/form";
    }

    @PostMapping("/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute EmployeeForm employeeForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", EmploymentStatus.values());
            model.addAttribute("pageTitle", "Edit Employee");
            return "employees/form";
        }

        employeeService.updateEmployee(id, employeeForm);
        redirectAttributes.addFlashAttribute("message", "Employee updated.");
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("message", "Employee deleted.");
        return "redirect:/employees";
    }
}
