package com.therealazimbek.spring.eventmasterapp.repositories;

import com.therealazimbek.spring.eventmasterapp.models.UserEvent;
import com.therealazimbek.spring.eventmasterapp.models.UserEventId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEventRepository extends JpaRepository<UserEvent, UserEventId> {
}
