package com.musicapp.controller;

import com.musicapp.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    // Service responsible for handling registration logic
    private final RegistrationService registrationService;
    // Constructor-based dependency injection
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            Model model) {
        // If registration succeeds, redirect to login page with a success query param
        if (registrationService.registerUser(email, username, password)) {
            return "redirect:/login?registered"; // registered successfully
        } else {
            // If email already exists, show error message on registration page
            model.addAttribute("error", "The email already exists");
            return "registration"; // Return to registration page displaying error
        }
    }
}