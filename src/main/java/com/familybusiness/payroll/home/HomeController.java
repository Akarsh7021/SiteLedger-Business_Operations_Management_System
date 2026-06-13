package com.familybusiness.payroll.home;

import com.familybusiness.payroll.user.AppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final AppUserService appUserService;

    public HomeController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/")
    public String home() {
        if (!appUserService.hasAnyUser()) {
            return "redirect:/register";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(Model model) {
        boolean setupRequired = !appUserService.hasAnyUser();
        if (setupRequired) {
            return "redirect:/register";
        }
        model.addAttribute("setupRequired", setupRequired);
        return "login";
    }
}
