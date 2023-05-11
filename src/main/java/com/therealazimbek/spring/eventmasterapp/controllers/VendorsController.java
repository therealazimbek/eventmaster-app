package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.Vendor;
import com.therealazimbek.spring.eventmasterapp.models.VendorCategory;
import com.therealazimbek.spring.eventmasterapp.repositories.VendorCategoryRepository;
import com.therealazimbek.spring.eventmasterapp.services.VendorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/vendors")
@Slf4j
public class VendorsController {

    private final VendorService vendorService;
    private final VendorCategoryRepository vendorCategoryRepository;

    public VendorsController(VendorService vendorService, VendorCategoryRepository vendorCategoryRepository) {
        this.vendorService = vendorService;
        this.vendorCategoryRepository = vendorCategoryRepository;
    }

    @ModelAttribute
    public Vendor vendor() {
        return new Vendor();
    }

    @ModelAttribute
    public VendorCategory category() {
        return new VendorCategory();
    }

    @GetMapping
    public String vendors(Model model) {
        model.addAttribute("vendors", vendorService.findAll());
        return "vendors";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("categories", vendorCategoryRepository.findAll());
        return "createVendor";
    }

    @PostMapping("/create")
    public String create(@Valid Vendor vendor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.toString());
            log.info(vendor.toString());
            return "createVendor";
        }
        vendor.setCategory(vendor.getCategory());
        vendorService.save(vendor);
        return "redirect:/vendors";
    }

    @GetMapping("/{id}")
    public String findVenueById(@PathVariable Long id, Model model) {
        Optional<Vendor> vendor = vendorService.findById(id);
        if (vendor.isPresent()) {
            model.addAttribute("vendor", vendor.get());
            return "vendor";
        }
        return "redirect:/notfound";
    }
}
