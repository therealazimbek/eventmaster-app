package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserEvent {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private UserRole userRole;
}
