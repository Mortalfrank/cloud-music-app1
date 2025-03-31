package com.musicapp.controller;

import com.musicapp.model.User;
import com.musicapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session
    ) {
        User user = authService.validateUser(email, password);
        if (user != null) {
            // The login is successful. The user name is saved to session
            session.setAttribute("username", user.getUserName());
            return "redirect:/";
        } else {
            model.addAttribute("error", "Email or password is invalid");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Clear the user information in the session.
        session.removeAttribute("username");
        return "redirect:/";
    }
}