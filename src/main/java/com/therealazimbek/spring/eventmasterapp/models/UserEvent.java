package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class UserEvent {

    public UserEvent(User user, Event event, UserRole userRole) {
        this.user = user;
        this.event = event;
        this.userRole = userRole;
    }

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
