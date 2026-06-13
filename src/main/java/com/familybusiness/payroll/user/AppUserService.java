package com.familybusiness.payroll.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean hasAnyUser() {
        return appUserRepository.count() > 0;
    }

    public boolean usernameExists(String username) {
        return appUserRepository.existsByUsername(username.trim());
    }

    @Transactional
    public void createFirstUser(SetupForm setupForm) {
        AppUser appUser = new AppUser();
        appUser.setUsername(setupForm.getUsername().trim());
        appUser.setPasswordHash(passwordEncoder.encode(setupForm.getPassword()));
        appUser.setCreatedAt(LocalDateTime.now());
        appUserRepository.save(appUser);
    }
}
