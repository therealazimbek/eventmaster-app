package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.repositories.UserRepository;
import com.therealazimbek.spring.eventmasterapp.utils.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @ModelAttribute
    public RegistrationForm registrationForm() {
        return new RegistrationForm();
    }

    public RegistrationController(
            UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping
    public String registerForm() {
        return "register";
    }

    @PostMapping
    public String processRegistration(@Valid RegistrationForm registrationForm, Errors errors
                                        , BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/register";
        }

        userRepo.save(registrationForm.toUser(passwordEncoder));
        return "redirect:/login";
    }
}
