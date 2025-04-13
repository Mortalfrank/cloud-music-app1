package com.musicapp.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.musicapp.model.User;
import com.musicapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.Item;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AmazonDynamoDB dynamoClient;
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session) {

        DynamoDB dynamoDB = new DynamoDB(dynamoClient);
        Table loginTable = dynamoDB.getTable("login");

        try {
            Item user = loginTable.getItem("email", email);
            if (user != null && user.getString("password").equals(password)) {
                session.setAttribute("userEmail", email);
                session.setAttribute("loggedIn", true);
                System.out.println("[DEBUG] login successfully, session: userEmail=" + email);
                return "redirect:/";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[ERROR] login failure，email=" + email);
        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Clear the user information in the session.
        session.removeAttribute("username");
        return "redirect:/";
    }
}