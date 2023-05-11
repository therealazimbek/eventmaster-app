package com.therealazimbek.spring.eventmasterapp.repositories;

import com.therealazimbek.spring.eventmasterapp.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Modifying
    @Query("delete from Task t where t.id = ?1")
    void deleteById(Long id);
}
