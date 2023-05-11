package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.*;
import com.therealazimbek.spring.eventmasterapp.repositories.EventRepository;
import com.therealazimbek.spring.eventmasterapp.repositories.TaskRepository;
import com.therealazimbek.spring.eventmasterapp.repositories.UserEventRepository;
import com.therealazimbek.spring.eventmasterapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final UserEventRepository userEventRepository;

    private final TaskRepository taskRepository;

    @Autowired
    public EventService(UserRepository userRepository, EventRepository eventRepository, UserEventRepository userEventRepository, TaskRepository taskRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userEventRepository = userEventRepository;
        this.taskRepository = taskRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Optional<Event> findById(UUID uuid) {
        return eventRepository.findById(uuid);
    }

    public List<UserEvent> findEventUsers(UUID id) {
        Event event = findById(id).get();
        return event.getEventUsers();
    }

    public List<Vendor> findAllEventVenues(UUID id) {
        Event event = findById(id).get();
        return event.getVendors();
    }

    public Venue findEventVendor(UUID id) {
        Event event = findById(id).get();
        return event.getVenue();
    }

    public void save(Event event) {
        eventRepository.save(event);
    }

    public Optional<Event> findByCode(String code) {
        return findAll().stream().filter(event -> event.getCode().equals(code)).findFirst();
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

    public List<Event> findAllActiveEventsExceptUser(String username) {
        return findAll().stream().filter(
                event -> !event.getIsPrivate() && event.getUser() != userRepository.findByUsername(username).get()
                        && (LocalDateTime.now().isBefore(event.getEndDate()) ||
                        LocalDateTime.now().isEqual(event.getStartDate()))
        ).collect(Collectors.toList());
    }

    public void delete(Event event) {
        event.setVendors(new ArrayList<>());
        event.getOrders().forEach(order -> order.setEvent(null));
        event.getTasks().forEach(task -> taskRepository.deleteById(task.getId()));
        event.setVenue(null);
        event.setUser(null);
        eventRepository.deleteById(event.getId());
    }
}
