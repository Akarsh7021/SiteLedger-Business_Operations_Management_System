package com.familybusiness.payroll.contractor;

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
@RequestMapping({"/customers", "/contractors"})
public class ContractorController {

    private final ContractorService contractorService;

    public ContractorController(ContractorService contractorService) {
        this.contractorService = contractorService;
    }

    @GetMapping
    public String listContractors(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("contractors", contractorService.findContractors(null));
        model.addAttribute("contractorRows", contractorService.findContractorRows(search));
        model.addAttribute("workSiteForm", new WorkSiteForm());
        addWorkSiteOptions(model);
        model.addAttribute("search", search);
        return "contractors/list";
    }

    @GetMapping("/{id}")
    public String contractorDetail(@PathVariable Long id, Model model) {
        model.addAttribute("contractor", contractorService.getContractor(id));
        addWorkSiteOptions(model);
        return "contractors/detail";
    }

    @GetMapping("/new")
    public String newContractor(Model model) {
        model.addAttribute("contractorForm", new ContractorForm());
        model.addAttribute("customerTypes", CustomerType.values());
        model.addAttribute("pageTitle", "Add Customer");
        return "contractors/form";
    }

    @PostMapping
    public String createContractor(
            @Valid @ModelAttribute ContractorForm contractorForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customerTypes", CustomerType.values());
            model.addAttribute("pageTitle", "Add Customer");
            return "contractors/form";
        }

        Contractor contractor = contractorService.createContractor(contractorForm);
        redirectAttributes.addFlashAttribute("message", "Customer added.");
        return "redirect:/customers/" + contractor.getId();
    }

    @GetMapping("/{id}/edit")
    public String editContractor(@PathVariable Long id, Model model) {
        Contractor contractor = contractorService.getContractor(id);
        model.addAttribute("contractorForm", ContractorForm.fromContractor(contractor));
        model.addAttribute("customerTypes", CustomerType.values());
        model.addAttribute("pageTitle", "Edit Customer");
        return "contractors/form";
    }

    @PostMapping("/{id}")
    public String updateContractor(
            @PathVariable Long id,
            @Valid @ModelAttribute ContractorForm contractorForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("customerTypes", CustomerType.values());
            model.addAttribute("pageTitle", "Edit Customer");
            return "contractors/form";
        }

        contractorService.updateContractor(id, contractorForm);
        redirectAttributes.addFlashAttribute("message", "Customer updated.");
        return "redirect:/customers/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteContractor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contractorService.deleteContractor(id);
        redirectAttributes.addFlashAttribute("message", "Customer deleted.");
        return "redirect:/customers";
    }

    @GetMapping("/{contractorId}/work-sites/new")
    public String newWorkSite(@PathVariable Long contractorId, Model model) {
        Contractor contractor = contractorService.getContractor(contractorId);
        WorkSiteForm form = new WorkSiteForm();
        form.setContractorId(contractorId);
        model.addAttribute("contractor", contractor);
        model.addAttribute("workSiteForm", form);
        model.addAttribute("pageTitle", "Add Work Site");
        addWorkSiteOptions(model);
        return "contractors/work-site-form";
    }

    @PostMapping("/{contractorId}/work-sites")
    public String createWorkSite(
            @PathVariable Long contractorId,
            @Valid @ModelAttribute WorkSiteForm workSiteForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("contractor", contractorService.getContractor(contractorId));
            model.addAttribute("pageTitle", "Add Work Site");
            addWorkSiteOptions(model);
            return "contractors/work-site-form";
        }

        contractorService.createWorkSite(contractorId, workSiteForm);
        redirectAttributes.addFlashAttribute("message", "Work site added.");
        return "redirect:/customers/" + contractorId;
    }

    @PostMapping("/work-sites")
    public String createSelectedContractorWorkSite(
            @Valid @ModelAttribute WorkSiteForm workSiteForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (workSiteForm.getContractorId() == null) {
            bindingResult.rejectValue("contractorId", "required", "Contractor is required");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Choose a contractor and enter valid work site details.");
            return "redirect:/customers";
        }

        contractorService.createWorkSite(workSiteForm.getContractorId(), workSiteForm);
        redirectAttributes.addFlashAttribute("message", "Work site added.");
        return "redirect:/customers";
    }

    @GetMapping("/{contractorId}/work-sites/{workSiteId}/edit")
    public String editWorkSite(@PathVariable Long contractorId, @PathVariable Long workSiteId, Model model) {
        Contractor contractor = contractorService.getContractor(contractorId);
        WorkSite workSite = contractorService.getWorkSite(workSiteId);
        model.addAttribute("contractor", contractor);
        model.addAttribute("workSiteForm", WorkSiteForm.fromWorkSite(workSite));
        model.addAttribute("pageTitle", "Edit Work Site");
        addWorkSiteOptions(model);
        return "contractors/work-site-form";
    }

    @PostMapping("/{contractorId}/work-sites/{workSiteId}")
    public String updateWorkSite(
            @PathVariable Long contractorId,
            @PathVariable Long workSiteId,
            @Valid @ModelAttribute WorkSiteForm workSiteForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("contractor", contractorService.getContractor(contractorId));
            model.addAttribute("pageTitle", "Edit Work Site");
            addWorkSiteOptions(model);
            return "contractors/work-site-form";
        }

        contractorService.updateWorkSite(contractorId, workSiteId, workSiteForm);
        redirectAttributes.addFlashAttribute("message", "Work site updated.");
        return "redirect:/customers";
    }

    @PostMapping("/{contractorId}/work-sites/{workSiteId}/delete")
    public String deleteWorkSite(
            @PathVariable Long contractorId,
            @PathVariable Long workSiteId,
            RedirectAttributes redirectAttributes
    ) {
        contractorService.deleteWorkSite(contractorId, workSiteId);
        redirectAttributes.addFlashAttribute("message", "Work site deleted.");
        return "redirect:/customers";
    }

    private void addWorkSiteOptions(Model model) {
        model.addAttribute("unitsOfMeasurement", UnitOfMeasurement.values());
        model.addAttribute("workSiteStatuses", WorkSiteStatus.values());
    }
}
