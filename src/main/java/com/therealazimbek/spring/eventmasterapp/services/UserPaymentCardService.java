package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.UserPaymentCard;
import com.therealazimbek.spring.eventmasterapp.repositories.UserPaymentCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserPaymentCardService {

    private final UserPaymentCardRepository repository;

    public UserPaymentCardService(UserPaymentCardRepository repository) {
        this.repository = repository;
    }

    public List<UserPaymentCard> findAll() {
        return repository.findAll();
    }

    public List<UserPaymentCard> findAllByUserId(String username) {
        return repository.findAll().stream().filter(card -> Objects.equals(card.getUser().getUsername(), username)).toList();
    }

    public void save(UserPaymentCard card) {
        repository.save(card);
    }
}
