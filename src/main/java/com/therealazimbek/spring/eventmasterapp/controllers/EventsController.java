package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.models.UserEvent;
import com.therealazimbek.spring.eventmasterapp.models.UserRole;
import com.therealazimbek.spring.eventmasterapp.services.EventService;
import com.therealazimbek.spring.eventmasterapp.services.UserService;
import com.therealazimbek.spring.eventmasterapp.services.VendorService;
import com.therealazimbek.spring.eventmasterapp.services.VenueService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/events")
@Slf4j
public class EventsController {

    private final UserService userService;

    private final EventService eventService;

    private final VendorService vendorService;

    private final VenueService venueService;

    @Autowired
    public EventsController(UserService userService, EventService eventService, VendorService vendorService, VenueService venueService) {
        this.eventService = eventService;
        this.userService = userService;
        this.vendorService = vendorService;
        this.venueService = venueService;
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
                ).toList());
    }

    @GetMapping
    public String events() {
        return "events";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("venues", venueService.findAll());
        return "createEvent";
    }

    @PostMapping("/create")
    public String create(@Valid Event event, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/createEvent";
        }
        event.setUser(userService.findByUsername(authentication.getName()));
        List<UserEvent> eventUsers = event.getEventUsers();
        eventUsers.add(new UserEvent(userService.findByUsername(authentication.getName()), event, UserRole.HOST));
        eventService.save(event);
        return "redirect:/events";
    }

    @GetMapping("/{id}")
    public String event(@PathVariable String id, Model model, Authentication authentication) {
        try {
            Event event = eventService.findById(UUID.fromString(id));
            model.addAttribute("event", event);
            model.addAttribute("user", userService.findByUsername(authentication.getName()));
            model.addAttribute("venue", event.getVenue());
            model.addAttribute("vendors", event.getVendors());
            model.addAttribute("allVendors", vendorService.findAll());
            return "event";
        } catch (ResponseStatusException e) {
            return "notfound";
        }
    }

    @GetMapping("/{id}/edit")
    public String update(@PathVariable String id, Model model, Authentication authentication) {
        try {
            Event event = eventService.findById(UUID.fromString(id));
            model.addAttribute("event", event);
            model.addAttribute("user", userService.findByUsername(authentication.getName()));
            model.addAttribute("vendors", vendorService.findAll());
            model.addAttribute("venues", venueService.findAll());
            return "updateEvent";
        } catch (ResponseStatusException e) {
            return "notfound";
        }
    }

    @PutMapping("/{id}/edit")
    public String update(@PathVariable String id, @Valid Event event) {
        try {
            Event urlEvent = eventService.findById(UUID.fromString(id));
            if(urlEvent.getId().equals(event.getId())) {
                event.setUser(urlEvent.getUser());

                eventService.save(event);

                return "events";
            } else {
                return "notfound";
            }
        } catch (ResponseStatusException e) {
            return "notfound";
        }
    }

    @PutMapping("/{id}/addVendor")
    public String addVendor(@PathVariable String id, Event event) {
        try {
            Event urlEvent = eventService.findById(UUID.fromString(id));
            log.info(String.valueOf(event.getVendors().size()));
            urlEvent.setVendors(event.getVendors());

            eventService.save(urlEvent);

            return "events";
        } catch (ResponseStatusException e) {
            return "notfound";
        }
    }

    @PutMapping("/join")
    public String join(String code, Authentication authentication) {
        Event event = eventService.findByCode(code);
        User user = userService.findByUsername(authentication.getName());
        if (event.getUser() != user && event.getMaxGuests().intValue() > event.getEventUsers().size()) {
            List<UserEvent> eventUsers = event.getEventUsers();
            eventUsers.add(new UserEvent(user, event, UserRole.GUEST));
            eventService.save(event);
            return "redirect:/events";
        }

        return "redirect:/events";
    }
}
