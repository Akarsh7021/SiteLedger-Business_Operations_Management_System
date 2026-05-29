package com.familybusiness.payroll.workhour;

import com.familybusiness.payroll.employee.EmployeeService;
import com.familybusiness.payroll.contractor.ContractorService;
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

import java.math.BigDecimal;

@Controller
@RequestMapping("/work-hours")
public class WorkHourController {

    private static final String RBC_ONLINE_BANKING_URL =
            "https://www.rbcroyalbank.com/ways-to-bank/online-banking/index.html";

    private final WorkHourService workHourService;
    private final EmployeeService employeeService;
    private final ContractorService contractorService;

    public WorkHourController(
            WorkHourService workHourService,
            EmployeeService employeeService,
            ContractorService contractorService
    ) {
        this.workHourService = workHourService;
        this.employeeService = employeeService;
        this.contractorService = contractorService;
    }

    @GetMapping
    public String listWorkHours(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            Model model
    ) {
        model.addAttribute("workHours", workHourService.findWorkHours(employeeId, paymentStatus));
        model.addAttribute("employees", employeeService.findEmployees(null));
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("selectedPaymentStatus", paymentStatus);
        model.addAttribute("rbcOnlineBankingUrl", RBC_ONLINE_BANKING_URL);
        return "work-hours/list";
    }

    @GetMapping("/new")
    public String newWorkHour(@RequestParam(required = false) Long employeeId, Model model) {
        WorkHourForm form = new WorkHourForm();
        form.setEmployeeId(employeeId);
        model.addAttribute("workHourForm", form);
        addFormOptions(model, "Add Work Hours");
        return "work-hours/form";
    }

    @PostMapping
    public String createWorkHour(
            @Valid @ModelAttribute WorkHourForm workHourForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addFormOptions(model, "Add Work Hours");
            return "work-hours/form";
        }

        workHourService.createWorkHour(workHourForm);
        redirectAttributes.addFlashAttribute("message", "Work hours added.");
        return "redirect:/work-hours";
    }

    @GetMapping("/{id}/edit")
    public String editWorkHour(@PathVariable Long id, Model model) {
        WorkHour workHour = workHourService.getWorkHour(id);
        if (workHour.getPaymentStatus() == PaymentStatus.PAID) {
            return "redirect:/work-hours";
        }
        model.addAttribute("workHourForm", WorkHourForm.fromWorkHour(workHour));
        addFormOptions(model, "Edit Work Hours");
        return "work-hours/form";
    }

    @PostMapping("/{id}")
    public String updateWorkHour(
            @PathVariable Long id,
            @Valid @ModelAttribute WorkHourForm workHourForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addFormOptions(model, "Edit Work Hours");
            return "work-hours/form";
        }

        workHourService.updateWorkHour(id, workHourForm);
        redirectAttributes.addFlashAttribute("message", "Work hours updated.");
        return "redirect:/work-hours";
    }

    @PostMapping("/{id}/pay")
    public String payWorkHour(
            @PathVariable Long id,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam(required = false) CashPaymentType cashPaymentType,
            @RequestParam(required = false) BigDecimal partialAmount,
            RedirectAttributes redirectAttributes
    ) {
        try {
            workHourService.markPaid(id, paymentMethod, cashPaymentType, partialAmount);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/work-hours";
        }
        String message = paymentMethod == PaymentMethod.CASH && cashPaymentType == CashPaymentType.PARTIAL
                ? "Partial cash payment saved."
                : "Work hours marked as paid.";
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/work-hours";
    }

    @PostMapping("/{id}/delete")
    public String deleteWorkHour(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        workHourService.deleteWorkHour(id);
        redirectAttributes.addFlashAttribute("message", "Work hours deleted.");
        return "redirect:/work-hours";
    }

    private void addFormOptions(Model model, String pageTitle) {
        model.addAttribute("employees", employeeService.findEmployees(null));
        model.addAttribute("customers", contractorService.findContractors(null));
        model.addAttribute("pageTitle", pageTitle);
    }
}
