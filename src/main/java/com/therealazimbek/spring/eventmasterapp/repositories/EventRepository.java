package com.therealazimbek.spring.eventmasterapp.repositories;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Modifying
    @Query("delete from Event e where e.id = ?1")
    void deleteById(UUID id);

}
