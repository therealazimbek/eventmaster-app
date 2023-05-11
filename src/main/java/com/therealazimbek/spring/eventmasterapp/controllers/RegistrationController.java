package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.services.UserService;
import com.therealazimbek.spring.eventmasterapp.utils.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @ModelAttribute
    public RegistrationForm registrationForm() {
        return new RegistrationForm();
    }

    public RegistrationController(
            UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String registerForm() {
        return "register";
    }

    @PostMapping
    public String processRegistration(@Valid RegistrationForm registrationForm
            , BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/register";
        }

        boolean uniqueValidation = false;

        if (userService.findByUsername(registrationForm.getUsername()).isPresent()) {
            model.addAttribute("usernameError", "Username already exists");
            uniqueValidation = true;
        }

        if (userService.findByEmail(registrationForm.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email already exists");
            uniqueValidation = true;
        }

        if (userService.findByPhone(registrationForm.getPhone()).isPresent()) {
            model.addAttribute("phoneError", "Phone already exists");
            uniqueValidation = true;
        }

        if (uniqueValidation) {
            return "/register";
        }

        userService.save(registrationForm.toUser(passwordEncoder));
        return "redirect:/login";
    }
}
