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

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private final UserEventService userEventService;

    @Autowired
    public EventsController(UserService userService, EventService eventService, VendorService vendorService, VenueService venueService, OrderService orderService, UserPaymentCardService userPaymentCardService, UserEventService userEventService) {
        this.eventService = eventService;
        this.userService = userService;
        this.vendorService = vendorService;
        this.venueService = venueService;
        this.orderService = orderService;
        this.userPaymentCardService = userPaymentCardService;
        this.userEventService = userEventService;
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
    public UserPaymentCard userPaymentCard() {
        return new UserPaymentCard();
    }

    @ModelAttribute
    public UserEvent userEvent() {
        return new UserEvent();
    }

    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("userEvents", userService.findAllActiveUserEvents(user.get()).stream().limit(4).toList());
            model.addAttribute("userJoinedEvents", userService.userJoinedEvents(user.get()));
            model.addAttribute("events",
                    eventService.findAllActiveEventsExceptUser(authentication.getName()).stream().limit(4).toList());
            model.addAttribute("userPaymentCards",
                    userPaymentCardService.findAllByUserId(authentication.getName()).stream().limit(4).toList());
            model.addAttribute("allEvents",
                    eventService.findAllActiveEventsExceptUser(authentication.getName()));
            model.addAttribute("venues", venueService.findAll());
            model.addAttribute("allVendors", vendorService.findAll());
        }
    }

    @GetMapping
    public String events() {
        return "events";
    }

    @GetMapping("/user")
    public String eventsUser(Model model, Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {
            model.addAttribute("userEvents", userService.findAllActiveUserEvents(user.get()));
            model.addAttribute("userPastEvents", user.get().getCreatedEvents().stream().filter(
                    event -> LocalDateTime.now().isAfter(event.getEndDate())
            ).toList());
            return "userEvents";
        }
        return "redirect:/notfound";
    }

    @GetMapping("/all")
    public String eventsAll() {
        return "allEvents";
    }

    @GetMapping("/create")
    public String create() {
        return "createEvent";
    }

    @PostMapping("/create")
    public String create(@Valid Event event, Model model, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "/createEvent";
        }
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {

            if (!LocalDateTime.now().isBefore(event.getStartDate()) ||
                    event.getEndDate().isBefore(event.getStartDate())) {
                model.addAttribute("endDateError", "Start date should be before end date");
                model.addAttribute("startDateError", "Start date should not be past date");
                return "/createEvent";
            }

            event.setUser(user.get());
            eventService.save(event);
            return "redirect:/events";
        }
        return "redirect:/notfound";
    }


    @GetMapping("/{id}")
    public String event(@PathVariable String id, Model model, Authentication authentication) {
        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        if (UUID_REGEX.matcher(id).matches()) {
            Optional<Event> event = eventService.findById(UUID.fromString(id));
            Optional<User> user = userService.findByUsername(authentication.getName());
            if (event.isPresent() && user.isPresent()) {
                if (event.get().getIsPrivate() && event.get().getUser() != user.get() && event.get().getEventUsers().stream().noneMatch(eventUser -> eventUser.getUser() == user.get())) {
                    return "redirect:/notfound";
                }
                model.addAttribute("event", event.get());
                model.addAttribute("user", user.get());
                model.addAttribute("availableUsers", userService.findAvailableUsersToAdd(user.get(), event.get()));
                model.addAttribute("venue", event.get().getVenue());
                model.addAttribute("vendors", event.get().getVendors());
                model.addAttribute("allVendors", vendorService.findAll().stream().filter(vendor -> !event.get().getVendors().contains(vendor)).toList());
                return "event";
            }
            return "redirect:/notfound";
        }
        return "redirect:/notfound";
    }

    @GetMapping("/{id}/edit")
    public String update(@PathVariable String id, Model model, Authentication authentication) {
        Optional<Event> event = eventService.findById(UUID.fromString(id));
        if (event.isPresent()) {
            if (Objects.equals(event.get().getUser().getUsername(), authentication.getName())) {
                model.addAttribute("event", event.get());
                model.addAttribute("user", userService.findByUsername(authentication.getName()));
                model.addAttribute("vendors", vendorService.findAll());
                model.addAttribute("venues", venueService.findAll());
                return "updateEvent";
            } else {
                return "redirect:/nopermission";
            }
        }
        return "redirect:/notfound";
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable String id, Authentication authentication) {
        Optional<Event> event = eventService.findById(UUID.fromString(id));
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (event.isPresent() && user.isPresent()) {
            if (Objects.equals(event.get().getUser().getUsername(), authentication.getName())) {
                userEventService.delete(user.get(), event.get());
                event.get().setVendors(new ArrayList<>());
                event.get().getOrders().forEach(order -> order.setEvent(null));
                event.get().getTasks().forEach(task -> task.setEvent(null));
                event.get().setVenue(null);
                event.get().setUser(null);
                eventService.delete(event.get());
                return "redirect:/events";
            } else {
                return "redirect:/nopermission";
            }
        }
        return "redirect:/notfound";
    }

    @PutMapping("/{id}/edit")
    public String update(@PathVariable String id, @Valid Event event, Authentication authentication) {
        Optional<Event> urlEvent = eventService.findById(UUID.fromString(id));
        if (urlEvent.isPresent()) {
            if (urlEvent.get().getId().equals(event.getId()) && Objects.equals(urlEvent.get().getUser().getUsername(), authentication.getName())) {
                event.setUser(urlEvent.get().getUser());

                eventService.save(event);

                return "redirect:/events/{id}";
            } else {
                return "redirect:/nopermission";
            }
        }
        return "redirect:/notfound";
    }

    @PutMapping("/{id}/addVendor")
    public String addVendor(@PathVariable String id, Event event) {
        Optional<Event> urlEvent = eventService.findById(UUID.fromString(id));
        if (urlEvent.isPresent()) {
            List<Vendor> existingVendors = urlEvent.get().getVendors();
            existingVendors.addAll(event.getVendors());
            urlEvent.get().setVendors(existingVendors.stream().distinct().collect(Collectors.toList()));
            eventService.save(urlEvent.get());
            return "redirect:/events/{id}";
        }
        return "redirect:/notfound";
    }

    @PutMapping("/{event_id}/addGuest")
    public String addGuest(@PathVariable String event_id, UserEvent guest) {
        Optional<Event> urlEvent = eventService.findById(UUID.fromString(event_id));
        if (urlEvent.isPresent()) {
            List<UserEvent> existingGuests = urlEvent.get().getEventUsers();
            existingGuests.add(new UserEvent(new UserEventId(guest.getUser().getId(), urlEvent.get().getId()), UserRole.GUEST, guest.getUser(), urlEvent.get()));
            urlEvent.get().setEventUsers(existingGuests);
            eventService.save(urlEvent.get());
            return "redirect:/events/{event_id}";
        }
        return "redirect:/notfound";
    }

    @PutMapping("/joinByCode")
    public String joinByCode(String code, Authentication authentication) {
        Optional<Event> event = eventService.findByCode(code);
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (event.isPresent() && user.isPresent()) {
            if (event.get().getUser() != user.get()
                    && event.get().getMaxGuests().intValue() > event.get().getEventUsers().size()) {
                eventService.addGuest(event.get(), user.get(), UserRole.GUEST);
                return "redirect:/events";
            }
            return "redirect:/nopermission";
        }
        return "redirect:/notfound";
    }

    @PutMapping("/{id}/join")
    public String joinUser(@PathVariable String id, Authentication authentication) {
        Optional<Event> event = eventService.findById(UUID.fromString(id));
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (event.isPresent() && user.isPresent()) {
            if (event.get().getUser() != user.get()) {
                eventService.addGuest(event.get(), user.get(), UserRole.GUEST);
                return "redirect:/events";
            }
            return "redirect:/nopermission";
        }
        return "redirect:/notfound";
    }

    @PostMapping("/{event_id}/buy")
    public String buyEventAndAddGuest(@PathVariable String event_id, UserPaymentCard userPaymentCard,
                                      Authentication authentication, Model model) {
        Optional<Event> event = eventService.findById(UUID.fromString(event_id));
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (event.isPresent() && user.isPresent()) {
            model.addAttribute("event", event);

            if (event.get().getUser() != user.get() && event.get().getMaxGuests().intValue() > event.get().getEventUsers().size()) {
                Order order = new Order(event.get().getPrice(), LocalDateTime.now(), user.get(), event.get());
                userPaymentCard.setUser(user.get());
                orderService.save(order);
                eventService.addGuest(event.get(), user.get(), UserRole.GUEST);
                return "redirect:/events/{event_id}";
            }
            return "redirect:/nopermission";
        }
        return "redirect:/notfound";
    }

    @PutMapping("/{id}/leave")
    public String leaveGuest(@PathVariable String id, Authentication authentication) {
        Optional<Event> event = eventService.findById(UUID.fromString(id));
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (event.isPresent() && user.isPresent()) {
            if (event.get().getUser() != user.get()) {
                eventService.removeGuest(event.get(), user.get());
                return "redirect:/events";
            }
            return "redirect:/nopermission";
        }
        return "redirect:/notfound";
    }
}
