package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.*;
import com.therealazimbek.spring.eventmasterapp.repositories.EventRepository;
import com.therealazimbek.spring.eventmasterapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Autowired
    public EventService(UserRepository userRepository, EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(UUID uuid) {
        if (eventRepository.existsById(uuid) && eventRepository.findById(uuid).isPresent()) {
            return eventRepository.findById(uuid).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<UserEvent> findEventUsers(UUID id) {
        Event event = findById(id);
        return event.getEventUsers();
    }

    public List<Vendor> findAllEventVenues(UUID id) {
        Event event = findById(id);
        return event.getVendors();
    }

    public Venue findEventVendor(UUID id) {
        Event event = findById(id);
        return event.getVenue();
    }

    public void save(Event event) {
        eventRepository.save(event);
    }
}
