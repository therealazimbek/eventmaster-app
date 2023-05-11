package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.*;
import com.therealazimbek.spring.eventmasterapp.services.EventService;
import com.therealazimbek.spring.eventmasterapp.services.UserService;
import com.therealazimbek.spring.eventmasterapp.services.VendorService;
import com.therealazimbek.spring.eventmasterapp.services.VenueService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final UserService userService;
    private final VenueService venueService;
    private final VendorService vendorService;
    private final EventService eventService;

    public HomeController(UserService userService, VenueService venueService, VendorService vendorService, EventService eventService) {
        this.userService = userService;
        this.venueService = venueService;
        this.vendorService = vendorService;
        this.eventService = eventService;
    }

    @ModelAttribute
    public Event event() {
        return new Event();
    }

    @ModelAttribute
    public Task task() {
        return new Task();
    }

    @ModelAttribute
    public Order userOrder() {
        return new Order();
    }

    @ModelAttribute
    public Vendor vendor() {
        return new Vendor();
    }

    @ModelAttribute
    public Venue venue() {
        return new Venue();
    }

    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("events", eventService.findAllActiveEventsExceptUser(user.get().getUsername()).stream().limit(4).toList());
            model.addAttribute("userEvents", user.get().getCreatedEvents().stream().filter(
                    event -> LocalDateTime.now().isBefore(event.getStartDate()) ||
                            LocalDateTime.now().isEqual(event.getStartDate())
            ).limit(4).toList());
            model.addAttribute("userTasks", user.get().getTasks().stream().filter(
                    task -> (LocalDateTime.now().isBefore(task.getDue()) ||
                            LocalDateTime.now().isEqual(task.getDue())) && task.getTaskStatus() != TaskStatus.COMPLETED
            ).limit(4).toList());
            model.addAttribute("userOrders", user.get().getOrders().stream().limit(4).toList());
            model.addAttribute("vendors", vendorService.findAll().stream().limit(4).toList());
            model.addAttribute("venues", venueService.findAll().stream().limit(4).toList());
        }
    }

    @GetMapping
    public String home() {
        return "home";
    }
}
