package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.Task;
import com.therealazimbek.spring.eventmasterapp.models.TaskStatus;
import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.repositories.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findAllUserTasks(Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(User::getTasks).orElse(null);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public void delete(Task task) {
        try {
            taskRepository.deleteById(task.getId());
//            taskRepository.deleteById(id);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void save(Task task) {
        try {
            taskRepository.save(task);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void update(Task task) {
        try {
            taskRepository.save(task);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void complete(Long id) {
        Optional<Task> task = findById(id);
        if (task.isPresent()) {
            task.get().setTaskStatus(TaskStatus.COMPLETED);
            update(task.get());
        }
    }
}
