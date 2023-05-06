package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.services.EventService;
import com.therealazimbek.spring.eventmasterapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/events")
public class EventsController {

    private final UserService userService;

    private final EventService eventService;

    @Autowired
    public EventsController(UserService userService, EventService eventService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @ModelAttribute
    public Event event() {
        return new Event();
    }

    @ModelAttribute
    public User user() {
        return new User();
    }


    @ModelAttribute
    public void userEvents(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("userEvents",user.getCreatedEvents());
    }

    @ModelAttribute
    public void allEvents(Model model, Authentication authentication) {
        model.addAttribute("events",
                eventService.findAll().stream().filter(
                        event -> event.getUser() != userService.findByUsername(authentication.getName())
                                    && (LocalDateTime.now().isBefore(event.getStartDate()) ||
                                        LocalDateTime.now().isEqual(event.getStartDate()))
                ));
    }

    @GetMapping
    public String events() {
        return "events";
    }

    @GetMapping("/create")
    public String create() {
        return "createEvent";
    }

    @PostMapping("/create")
    public String create(@Valid Event event, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/createEvent";
        }
        event.setUser(userService.findByUsername(authentication.getName()));
        eventService.save(event);
        return "redirect:/events";
    }

    @GetMapping("/{id}")
    public String event(@PathVariable String id, Model model, Authentication authentication) {
        try {
            Event event = eventService.findById(UUID.fromString(id));
            model.addAttribute("event", event);
            model.addAttribute("user", userService.findByUsername(authentication.getName()));
            return "event";
        } catch (ResponseStatusException e) {
            return "notfound";
        }
    }
}
