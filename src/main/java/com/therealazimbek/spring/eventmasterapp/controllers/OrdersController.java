package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.services.OrderService;
import com.therealazimbek.spring.eventmasterapp.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final UserService userService;

    private final OrderService orderService;

    public OrdersController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("orders", orderService.findAllUserOrders(user.get().getId()));
        }
    }

    @GetMapping
    public String orders() {
        return "orders";
    }
}
