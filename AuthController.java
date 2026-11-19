package com.researchintelligence.controller;

import com.researchintelligence.model.User;
import com.researchintelligence.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "registered", required = false) String registered,
                                Model model) {
        if ("invalid".equals(error)) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        } else if ("session_expired".equals(error)) {
            model.addAttribute("errorMessage", "Please log in to access the workspace.");
        }

        if ("success".equals(registered)) {
            model.addAttribute("successMessage", "Account created successfully! Please sign in.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             HttpSession session,
                             Model model) {
        Optional<User> userOpt = userService.authenticate(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("loggedInUser", user);
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin";
            }
            return "redirect:/dashboard";
        }

        model.addAttribute("errorMessage", "Invalid email or password.");
        model.addAttribute("email", email);
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                @RequestParam(value = "role", defaultValue = "USER") String role,
                                Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            return "register";
        }

        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Password must be at least 6 characters long.");
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            return "register";
        }

        try {
            userService.registerUser(name, email, password, role);
            return "redirect:/login?registered=success";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=success";
    }
}
