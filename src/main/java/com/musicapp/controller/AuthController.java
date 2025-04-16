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

    /**
     * Displays the login page when a GET request is made to /login.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // Returns the login view (login.html or login.jsp)
    }

    /**
     * Handles login form submission.
     * Authenticates user using DynamoDB and sets session attributes.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @param session  The current HTTP session.
     * @param model    Spring MVC Model to pass data to the view.
     * @return Redirects to home if successful, otherwise returns to login page with error.
     */
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        // Create a DynamoDB instance to interact with the table
        DynamoDB dynamoDB = new DynamoDB(dynamoClient);
        Table loginTable = dynamoDB.getTable("login");

        try {
            // Retrieve user item by email from the login table
            Item user = loginTable.getItem("email", email);

            // If user exists and password matches
            if (user != null && user.getString("password").equals(password)) {
                // Store user info in session attributes
                session.setAttribute("userEmail", email);
                session.setAttribute("username", user.getString("user_name"));
                session.setAttribute("loggedIn", true);
                System.out.println("[DEBUG] Login successful. Session: " + session.getId());
                return "redirect:/";  // Redirect to homepage
            }
        } catch (Exception e) {
            // Log error and stack trace for debugging
            System.err.println("[ERROR] Login failed for email: " + email);
            e.printStackTrace();
        }

        // If login fails, return to login page with error message
        model.addAttribute("error", "Invalid email or password.");
        return "login";
    }

    /**
     * Logs the user out by invalidating the session.
     *
     * @param session The current HTTP session.
     * @return Redirects to the home page.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Destroy all session data
        return "redirect:/";   // Redirect to homepage
    }
}
