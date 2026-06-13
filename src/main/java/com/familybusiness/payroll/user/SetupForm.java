package com.familybusiness.payroll.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SetupForm {

    @NotBlank(message = "Username is required")
    @Size(max = 80, message = "Username must be 80 characters or less")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 72, message = "Password must be between 6 and 72 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
