package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.models.UserEventId;
import com.therealazimbek.spring.eventmasterapp.repositories.UserEventRepository;
import org.springframework.stereotype.Service;

@Service
public class UserEventService {

    private final UserEventRepository repository;

    public UserEventService(UserEventRepository repository) {
        this.repository = repository;
    }

    public void delete(User user, Event event) {
        repository.deleteById(new UserEventId(user.getId(), event.getId()));
    }
}
