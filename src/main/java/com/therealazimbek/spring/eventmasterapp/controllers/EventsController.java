package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.*;
import com.therealazimbek.spring.eventmasterapp.services.*;
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
import java.util.*;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/events")
@Slf4j
public class EventsController {

    private final UserService userService;

    private final EventService eventService;

    private final VendorService vendorService;

    private final VenueService venueService;

    private final OrderService orderService;

    private final UserPaymentCardService userPaymentCardService;

    @Autowired
    public EventsController(UserService userService, EventService eventService, VendorService vendorService, VenueService venueService, OrderService orderService, UserPaymentCardService userPaymentCardService) {
        this.eventService = eventService;
        this.userService = userService;
        this.vendorService = vendorService;
        this.venueService = venueService;
        this.orderService = orderService;
        this.userPaymentCardService = userPaymentCardService;
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
        model.addAttribute("userEvents",userService.findAllActiveUserEvents(user).stream().limit(4).toList());
    }

    @ModelAttribute
    public void userJoinedEvents(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("userJoinedEvents", userService.userJoinedEvents(user));
    }

    @ModelAttribute
    public void allEvents(Model model, Authentication authentication) {
        model.addAttribute("events",
                eventService.findAllActiveEventsExceptUser(authentication.getName()).stream().limit(4).toList());
    }

    @ModelAttribute
    public UserPaymentCard userPaymentCard() {
        return new UserPaymentCard();
    }

    @ModelAttribute
    public UserEvent userEvent() {
        return new UserEvent();
    }

    @ModelAttribute
    public void userPaymentCards(Model model, Authentication authentication) {
        model.addAttribute("userPaymentCards",
                userPaymentCardService.findAllByUserId(authentication.getName()).stream().limit(4).toList());
    }

    @GetMapping
    public String events() {
        return "events";
    }

    @GetMapping("/user")
    public String eventsUser(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("userEvents", userService.findAllActiveUserEvents(user));
        model.addAttribute("userPastEvents", user.getCreatedEvents().stream().filter(
                event -> LocalDateTime.now().isAfter(event.getEndDate())
        ).toList());
        return "userEvents";
    }

    @GetMapping("/all")
    public String eventsAll(Model model, Authentication authentication) {
        model.addAttribute("allEvents",
                eventService.findAllActiveEventsExceptUser(authentication.getName()));
        return "allEvents";
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
        event.setVendors(new ArrayList<>());
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
                User user = userService.findByUsername(authentication.getName());

                if(event.getIsPrivate() && event.getUser() != user && event.getEventUsers().stream().noneMatch(eventUser -> eventUser.getUser() == user)) {
                    return "redirect:/notfound";
                }

                model.addAttribute("event", event);
                model.addAttribute("user", user);
                model.addAttribute("availableUsers", userService.findAvailableUsersToAdd(user, event).stream()
                        .map(u -> new UserEvent(new UserEventId(u.getId(), event.getId()), UserRole.GUEST, u, event)).toList());
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

//    @PutMapping("/{event_id}/addGuest")
//    public String addGuest(@PathVariable String event_id, Event event) {
//        try {
//            Event urlEvent = eventService.findById(UUID.fromString(event_id));
//            List<UserEvent> guests = urlEvent.getEventUsers();
//            HashSet<UserEvent> hashSet = new HashSet<>(guests);
//            log.info(event.toString());
////            log.info(user.getUsername());
////            hashSet.addAll(event.getVendors());
////            urlEvent.setVendors(hashSet.stream().toList());
////            eventService.save(urlEvent);
//
//            return "events";
//        } catch (ResponseStatusException e) {
//            return "redirect:/notfound";
//        }
//    }

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

    @PostMapping("/{event_id}/buy")
    public String buyEventAndAddGuest(@PathVariable String event_id, UserPaymentCard userPaymentCard,
                                      Authentication authentication, Model model) {

        try {
            Event event = eventService.findById(UUID.fromString(event_id));
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("event", event);

            if (event.getUser() != user && event.getMaxGuests().intValue() > event.getEventUsers().size()) {
                Order order = new Order(event.getPrice(), LocalDateTime.now(), user, event);
                userPaymentCard.setUser(user);
                orderService.save(order);
                eventService.addGuest(event, user, UserRole.GUEST);
//                log.info(order.toString());
//                log.info(userPaymentCard.toString());
                return "redirect:/events/{event_id}";
            }
            return "redirect:/nopermission";
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
