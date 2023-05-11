package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.models.UserEvent;
import com.therealazimbek.spring.eventmasterapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<Event> findAllUserEvents(Long id) {
        User user = findById(id).get();
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
        availableUsers.remove(user);
        return availableUsers;
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
