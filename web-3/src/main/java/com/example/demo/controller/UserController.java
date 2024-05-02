package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.ForgotPasswordService;
import com.example.demo.service.ResetPasswordService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) { //display users in index.jsp
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "index";
    }

  

    @PostMapping("/user")
    public String saveUser(User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

   

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

    @PostMapping("/users/deleteAll")
    public String deleteAllUsers() {
        userService.deleteAllUsers();
        return "redirect:/users";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user) {
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(User user, Model model) {
        User loggedInUser = userService.getUserByEmailAndPassword(user.getEmail(), user.getPassword());
        if (loggedInUser != null) {
            return "redirect:/index";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }
    @Autowired
    private ResetPasswordService resetPasswordService;
    @GetMapping("/resetpassword")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "resetpassword";
    }


    @PostMapping("/resetpassword")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword) {
        // Add validation for password matching if needed
        if (!password.equals(confirmPassword)) {
            // Handle password mismatch error
            return "redirect:/resetpassword?error=passwordMismatch";
        }

        resetPasswordService.resetPassword(token, password);
        return "redirect:/login"; // Redirect to the login page after successful password reset
    }

    // You can add additional mappings or error handling as needed

    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @GetMapping("/forgotpassword")
    public String showForgotPasswordForm(Model model) {
        return "forgotpassword"; // Assuming "forgot-password.html" is the name of your forgot password page
    }
    @GetMapping("/forgotpasswordsuccess")
    public String showForgotPasswordSuccessPage() {
        return "forgotpasswordsuccess";
    }



    @PostMapping("/forgotpassword")
    public String forgotPassword(@RequestParam("email") String email) {
        // Call the service to handle the forgot password logic
        String resetToken = forgotPasswordService.forgotPassword(email);

        // If resetToken is null, it means the email doesn't exist in the database
        if (resetToken == null) {
            // Handle case where email is not found
            // You can throw an exception, return a specific response, or log the event
            return "redirect:/forgotpassword?error=emailNotFound";
        }

        // Construct the reset password URL using ServletUriComponentsBuilder
        String resetPasswordUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/resetpassword")
                .queryParam("token", resetToken)
                .toUriString();

        // Send an email with the reset password link
        String emailContent = "Please click the following link to reset your password: " + resetPasswordUrl;
        emailService.sendEmail(email, "Password Reset", emailContent);

        return "redirect:/forgotpasswordsuccess"; // Redirect to a success page
    }

    // You can add additional mappings or error handling as needed

}
