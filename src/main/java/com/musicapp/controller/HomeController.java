package com.musicapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    // Home Page
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Get the login status from the session.
        String username = (String) session.getAttribute("username");

        if (username != null) {
            model.addAttribute("message", "Welcome " + username);
        } else {
            model.addAttribute("message", "Welcome to Music App");
        }
        return "home";
    }
}