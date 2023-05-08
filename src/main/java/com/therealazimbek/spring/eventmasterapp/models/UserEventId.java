package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class UserEventId implements Serializable {

    public UserEventId(Long userId, UUID eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    @Column(name = "user_id")
    Long userId;

    @Column(name = "eventId")
    UUID eventId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEventId that = (UserEventId) o;

        if (!Objects.equals(userId, that.userId)) return false;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        return result;
    }
}
