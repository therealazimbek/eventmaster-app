package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.*;
import com.therealazimbek.spring.eventmasterapp.repositories.EventRepository;
import com.therealazimbek.spring.eventmasterapp.repositories.UserEventRepository;
import com.therealazimbek.spring.eventmasterapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final UserEventRepository userEventRepository;

    @Autowired
    public EventService(UserRepository userRepository, EventRepository eventRepository, UserEventRepository userEventRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userEventRepository = userEventRepository;
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

    public Event findByCode(String code) {
        Optional<Event> e = findAll().stream().filter(event -> event.getCode().equals(code)).findFirst();
        if (e.isPresent()) {
            return e.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void addGuest(Event event, User user, UserRole host) {
        if (event.getId() != null && user != null) {
            List<UserEvent> eventUsers = event.getEventUsers();
            UserEventId userEventId = new UserEventId(user.getId(), event.getId());
            UserEvent guest = new UserEvent(userEventId, host, user, event);
            eventUsers.add(guest);
            eventRepository.save(event);
        }
    }

    public void removeGuest(Event event, User user) {
        if (event.getId() != null && user != null && event.getUser() != user) {
            user.getUserEvents().removeIf(e -> e.getEvent().getId() == event.getId());
            UserEvent userEvent = userEventRepository.findAll().stream().filter(e -> e.getEvent() == event && e.getUser() == user)
                    .findFirst().get();
            userEventRepository.delete(userEvent);
        }
    }

    public void delete(String id) {
        eventRepository.deleteById(UUID.fromString(id));
    }
}
