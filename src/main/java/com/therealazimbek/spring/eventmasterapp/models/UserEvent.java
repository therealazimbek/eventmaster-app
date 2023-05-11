package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class UserEvent {

    public UserEvent(UserEventId id, UserRole userRole, User user, Event event) {
        this.userEventId = id;
        this.userRole = userRole;
        this.event = event;
        this.user = user;
    }

    @EmbeddedId
    private UserEventId userEventId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    private UserRole userRole;
}
