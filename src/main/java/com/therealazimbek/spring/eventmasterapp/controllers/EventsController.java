package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

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
    public void userJoinedEvents(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("userJoinedEvents", userService.userJoinedEvents(user));
    }

    @ModelAttribute
    public void allEvents(Model model, Authentication authentication) {
        model.addAttribute("events",
                eventService.findAll().stream().filter(
                        event -> !event.getIsPrivate() && event.getUser() != userService.findByUsername(authentication.getName())
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
        User user = userService.findByUsername(authentication.getName());
        event.setUser(user);
        eventService.save(event);
        return "redirect:/events";
    }



    @GetMapping("/{id}")
    public String event(@PathVariable String id, Model model, Authentication authentication) {
        try {
            Pattern UUID_REGEX =
                    Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

            if (UUID_REGEX.matcher(id).matches()) {
                UUID uuid = UUID.fromString(id);
                Event event = eventService.findById(uuid);
                model.addAttribute("event", event);
                model.addAttribute("user", userService.findByUsername(authentication.getName()));
                model.addAttribute("venue", event.getVenue());
                model.addAttribute("vendors", event.getVendors());
                model.addAttribute("allVendors", vendorService.findAll());
                return "event";
            }
            return "redirect:/notfound";
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @GetMapping("/{id}/edit")
    public String update(@PathVariable String id, Model model, Authentication authentication) {
        try {
            Event event = eventService.findById(UUID.fromString(id));
            if (Objects.equals(event.getUser().getUsername(), authentication.getName())) {
                model.addAttribute("event", event);
                model.addAttribute("user", userService.findByUsername(authentication.getName()));
                model.addAttribute("vendors", vendorService.findAll());
                model.addAttribute("venues", venueService.findAll());
                return "updateEvent";
            } else {
                return "redirect:/nopermission";
            }

        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable String id, Authentication authentication) {
        try {
            Event event = eventService.findById(UUID.fromString(id));
            if (Objects.equals(event.getUser().getUsername(), authentication.getName())) {
                eventService.delete(id);
                return "redirect:/events";
            } else {
                return "redirect:/nopermission";
            }

        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @PutMapping("/{id}/edit")
    public String update(@PathVariable String id, @Valid Event event, Authentication authentication) {
        try {
            Event urlEvent = eventService.findById(UUID.fromString(id));
            if(urlEvent.getId().equals(event.getId()) && Objects.equals(urlEvent.getUser().getUsername(), authentication.getName())) {
                event.setUser(urlEvent.getUser());

                eventService.save(event);

                return "events";
            } else {
                return "redirect:/nopermission";
            }
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @PutMapping("/{id}/addVendor")
    public String addVendor(@PathVariable String id, Event event) {
        try {
            Event urlEvent = eventService.findById(UUID.fromString(id));
            log.info(String.valueOf(event.getVendors().size()));
            List<Vendor> vendors = urlEvent.getVendors();
            HashSet<Vendor> hashSet = new HashSet<>(vendors);
            hashSet.addAll(event.getVendors());
            urlEvent.setVendors(hashSet.stream().toList());
            eventService.save(urlEvent);

            return "events";
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @PutMapping("/joinByCode")
    public String joinByCode(String code, Authentication authentication) {
        Event event = eventService.findByCode(code);
        User user = userService.findByUsername(authentication.getName());
        if (event.getUser() != user && event.getMaxGuests().intValue() > event.getEventUsers().size()) {
            eventService.addGuest(event, user, UserRole.GUEST);
            return "redirect:/events";
        }

        return "redirect:/nopermission";
    }

    @PutMapping("/{id}/join")
    public String joinUser(@PathVariable String id, Authentication authentication) {
        try {
            Event event = eventService.findById(UUID.fromString(id));
            User user = userService.findByUsername(authentication.getName());
            if (event.getUser() != user && event.getMaxGuests().intValue() > event.getEventUsers().size()) {
                eventService.addGuest(event, user, UserRole.GUEST);
                return "redirect:/events/{id}";
            }
            return "redirect:/notfound";
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @PutMapping("/{id}/leave")
    public String leaveGuest(@PathVariable String id, Authentication authentication) {
        try {
            Event event = eventService.findById(UUID.fromString(id));
            User user = userService.findByUsername(authentication.getName());
            if (event.getUser() != user) {
                eventService.removeGuest(event, user);
                return "redirect:/events";
            }
            return "redirect:/notfound";
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }
}
