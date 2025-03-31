package com.musicapp.controller;

import com.musicapp.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

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

        if (registrationService.registerUser(email, username, password)) {
            return "redirect:/login?registered"; // registered successfully
        } else {
            model.addAttribute("error", "The email already exists");
            return "registration"; // Return to registration page displaying error
        }
    }
}