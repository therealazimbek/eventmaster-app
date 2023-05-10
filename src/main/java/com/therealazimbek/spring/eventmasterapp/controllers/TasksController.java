package com.therealazimbek.spring.eventmasterapp.controllers;

import com.therealazimbek.spring.eventmasterapp.models.Event;
import com.therealazimbek.spring.eventmasterapp.models.Task;
import com.therealazimbek.spring.eventmasterapp.models.TaskStatus;
import com.therealazimbek.spring.eventmasterapp.models.User;
import com.therealazimbek.spring.eventmasterapp.services.TaskCategoryService;
import com.therealazimbek.spring.eventmasterapp.services.TaskService;
import com.therealazimbek.spring.eventmasterapp.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/tasks")
@Slf4j
public class TasksController {

    private final TaskService taskService;

    private final UserService userService;

    private final TaskCategoryService taskCategoryService;

    public TasksController(TaskService taskService, UserService userService, TaskCategoryService taskCategoryService) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskCategoryService = taskCategoryService;
    }

    @ModelAttribute
    public Task task() {
        return new Task();
    }

    @GetMapping
    public String tasks(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("activeTasks", user.getTasks().stream().filter(
                task -> !task.getTaskStatus().equals(TaskStatus.COMPLETED)
        ).toList());
        model.addAttribute("completedTasks", user.getTasks().stream().filter(
                task -> task.getTaskStatus().equals(TaskStatus.COMPLETED)
        ).toList());
        return "tasks";
    }

    @GetMapping("/create")
    public String create(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("events", user.getCreatedEvents().stream().filter(
                event -> LocalDateTime.now().isBefore(event.getStartDate()) ||
                        LocalDateTime.now().isEqual(event.getStartDate())
        ).toList());
        model.addAttribute("categories", taskCategoryService.findAll());
        return "createTask";
    }

    @PostMapping("/create")
    public String create(@Valid Task task, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "createTask";
        }
        User user = userService.findByUsername(authentication.getName());
        task.setUser(user);
        taskService.save(task);
//        log.info(task.toString());
        return "redirect:/tasks";
    }

    @GetMapping("/{id}")
    public String task(@PathVariable String id, Model model, Authentication authentication) {
        try {
            Task task = taskService.findById(Long.valueOf(id));
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("task", task);
            model.addAttribute("user", user);
            return "task";
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model, Authentication authentication) {
        try {
            Task task = taskService.findById(Long.valueOf(id));
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("task", task);
            model.addAttribute("user", user);
            model.addAttribute("events", user.getCreatedEvents().stream().filter(
                    event -> LocalDateTime.now().isBefore(event.getStartDate()) ||
                            LocalDateTime.now().isEqual(event.getStartDate())
            ).toList());
            model.addAttribute("categories", taskCategoryService.findAll());
            return "updateTask";
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @PutMapping("/{id}/edit")
    public String edit(@PathVariable String id, @Valid Task t, Model model, Authentication authentication) {
        try {
            Task task = taskService.findById(Long.valueOf(id));
            User user = userService.findByUsername(authentication.getName());

            if (Objects.equals(task.getUser().getUsername(), user.getUsername())) {
                model.addAttribute("task", task);
                model.addAttribute("user", user);
                model.addAttribute("events", user.getCreatedEvents().stream().filter(
                        event -> LocalDateTime.now().isBefore(event.getStartDate()) ||
                                LocalDateTime.now().isEqual(event.getStartDate())
                ).toList());
                model.addAttribute("categories", taskCategoryService.findAll());
                t.setUser(user);
                taskService.update(t);
                return "redirect:/tasks";
            }
            return "redirect:/nopermission";
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable String id, Authentication authentication) {
        try {
            Task task = taskService.findById(Long.valueOf(id));
            if (Objects.equals(task.getUser().getUsername(), authentication.getName())) {
                taskService.delete(Long.valueOf(id));
                return "redirect:/tasks";
            } else {
                return "redirect:/nopermission";
            }

        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }

    @PutMapping("/{id}/complete")
    public String complete(@PathVariable String id, Authentication authentication) {
        try {
            Task task = taskService.findById(Long.valueOf(id));
            if(Objects.equals(task.getUser().getUsername(), authentication.getName())) {
                taskService.complete(Long.valueOf(id));
                return "redirect:/tasks";
            } else {
                return "redirect:/nopermission";
            }
        } catch (ResponseStatusException e) {
            return "redirect:/notfound";
        }
    }
}
