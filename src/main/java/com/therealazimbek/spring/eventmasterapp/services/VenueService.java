package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.Venue;
import com.therealazimbek.spring.eventmasterapp.repositories.VenueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    public Optional<Venue> findById(Long id) {
        return venueRepository.findById(id);
    }

    public List<Event> findAllVenueEvents(Long id) {
        Optional<Venue> venue = findById(id);
        return venue.map(Venue::getEvents).orElse(null);
    }

    public void save(Venue venue) {
        try {
            venueRepository.save(venue);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
