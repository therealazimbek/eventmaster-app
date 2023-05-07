package com.therealazimbek.spring.eventmasterapp.repositories;

import com.therealazimbek.spring.eventmasterapp.models.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {
}
