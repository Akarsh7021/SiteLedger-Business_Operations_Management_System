package com.familybusiness.payroll.sitehour;

import com.familybusiness.payroll.contractor.WorkSiteStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/site-hours")
public class SiteHourController {

    private final SiteHourService siteHourService;

    public SiteHourController(SiteHourService siteHourService) {
        this.siteHourService = siteHourService;
    }

    @GetMapping
    public String listSiteHours(Model model) {
        model.addAttribute("siteHours", siteHourService.findSiteHourSummaries());
        model.addAttribute("workSiteStatuses", WorkSiteStatus.values());
        return "site-hours/list";
    }

    @PostMapping("/{workSiteId}/status")
    public String updateStatus(
            @PathVariable Long workSiteId,
            @RequestParam WorkSiteStatus status,
            RedirectAttributes redirectAttributes
    ) {
        siteHourService.updateStatus(workSiteId, status);
        redirectAttributes.addFlashAttribute("message", "Site status updated.");
        return "redirect:/site-hours";
    }
}
