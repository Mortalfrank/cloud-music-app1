package com.musicapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Home Page
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Music App!");
        return "home"; // 对应 templates/home.html.html
    }

    // Login Page
    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html.html
    }
}