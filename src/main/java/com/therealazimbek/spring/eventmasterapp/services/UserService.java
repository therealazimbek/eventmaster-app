package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.models.UserEvent;
import com.therealazimbek.spring.eventmasterapp.repositories.EventRepository;
import com.therealazimbek.spring.eventmasterapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Autowired
    public UserService(UserRepository userRepository, EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        if (userRepository.existsById(id) && userRepository.findById(id).isPresent()) {
            return userRepository.findById(id).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public User findByUsername(String username) {
        if (userRepository.findByUsername(username) != null) {
            return userRepository.findByUsername(username);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<Event> findAllUserEvents(Long id) {
        User user = findById(id);
        return user.getCreatedEvents();
    }

    public List<Event> userJoinedEvents(User user) {
        List<Event> joinedEvents = new ArrayList<>();
        user.getUserEvents().forEach(
                userEvent -> joinedEvents.add(userEvent.getEvent())
        );
        return joinedEvents;
    }

    public List<Event> findAllActiveUserEvents(User user) {
        return user.getCreatedEvents().stream().filter(
                event -> LocalDateTime.now().isBefore(event.getEndDate()) ||
                        LocalDateTime.now().isEqual(event.getStartDate())
        ).collect(Collectors.toList());
    }

    public List<User> findAvailableUsersToAdd(User user, Event event) {
        List<User> existingGuest = event.getEventUsers().stream().map(UserEvent::getUser).toList();
        List<User> availableUsers = findAll();
        availableUsers.removeAll(existingGuest);
        return availableUsers;
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
