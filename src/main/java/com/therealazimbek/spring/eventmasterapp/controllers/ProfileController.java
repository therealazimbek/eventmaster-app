package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.models.UserPaymentCard;
import com.therealazimbek.spring.eventmasterapp.services.UserPaymentCardService;
import com.therealazimbek.spring.eventmasterapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    private final UserPaymentCardService userPaymentCardService;

    public ProfileController(UserService userService, UserPaymentCardService userPaymentCardService) {
        this.userService = userService;
        this.userPaymentCardService = userPaymentCardService;
    }

    @ModelAttribute
    public User user() {
        return new User();
    }

    @ModelAttribute
    public UserPaymentCard userPaymentCard() {
        return new UserPaymentCard();
    }

    @GetMapping
    public String profile(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/addCard")
    public String addCard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        return "cardForm";
    }

    @PostMapping("/addCard")
    public String addCard(@Valid UserPaymentCard userPaymentCard, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "cardForm";
        }
        User user = userService.findByUsername(authentication.getName());
        userPaymentCard.setUser(user);
        userPaymentCardService.save(userPaymentCard);
        return "redirect:/profile";
    }
}
