package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.Vendor;
import com.therealazimbek.spring.eventmasterapp.repositories.VendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VendorService {

    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public List<Vendor> findAll() {
        return vendorRepository.findAll();
    }

    public Optional<Vendor> findById(Long id) {
        return vendorRepository.findById(id);
    }

    public List<Event> findVendorEvents(Long id) {
        Optional<Vendor> vendor = findById(id);
        return vendor.map(Vendor::getEvents).orElse(null);
    }

    public void save(Vendor vendor) {
        try {
            vendorRepository.save(vendor);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
