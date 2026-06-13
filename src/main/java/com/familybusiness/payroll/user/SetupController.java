package com.familybusiness.payroll.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
public class SetupController {

    private final AppUserService appUserService;

    public SetupController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (appUserService.hasAnyUser()) {
            return "redirect:/login";
        }
        if (!model.containsAttribute("setupForm")) {
            model.addAttribute("setupForm", new SetupForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String createAccount(
            @Valid @ModelAttribute SetupForm setupForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (appUserService.hasAnyUser()) {
            return "redirect:/login";
        }
        if (!Objects.equals(setupForm.getPassword(), setupForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
        }
        if (!bindingResult.hasFieldErrors("username") && appUserService.usernameExists(setupForm.getUsername())) {
            bindingResult.rejectValue("username", "username.exists", "Username is already used");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        appUserService.createFirstUser(setupForm);
        redirectAttributes.addAttribute("registered", "true");
        return "redirect:/login";
    }
}
