package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.TaskCategory;
import com.therealazimbek.spring.eventmasterapp.repositories.TaskCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskCategoryService {

    private final TaskCategoryRepository repository;

    public TaskCategoryService(TaskCategoryRepository repository) {
        this.repository = repository;
    }

    public List<TaskCategory> findAll() {
        return repository.findAll();
    }
}
