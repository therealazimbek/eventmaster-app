package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.Venue;
import com.therealazimbek.spring.eventmasterapp.services.VenueService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/venues")
public class VenuesController {

    private final VenueService venueService;

    public VenuesController(VenueService venueService) {
        this.venueService = venueService;
    }

    @ModelAttribute
    public Venue venue() {
        return new Venue();
    }

    @GetMapping
    public String venues(Model model) {
        model.addAttribute("venues", venueService.findAll());
        return "venues";
    }

    @GetMapping("/create")
    public String create() {
        return "createVenue";
    }

    @PostMapping("/create")
    public String create(@Valid Venue venue, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "createVenue";
        }

        venueService.save(venue);
        return "redirect:/venues";
    }

    @GetMapping("/{id}")
    public String findVenueById(@PathVariable Long id, Model model) {
        Optional<Venue> venue = venueService.findById(id);
        if (venue.isPresent()) {
            model.addAttribute("venue", venue.get());
            return "venue";
        } else {
            return "redirect:/notfound";
        }
    }
}
