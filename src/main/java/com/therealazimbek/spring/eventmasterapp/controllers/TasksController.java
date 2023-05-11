package com.therealazimbek.spring.eventmasterapp.controllers;

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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

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

    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("activeTasks", user.get().getTasks().stream().filter(
                    task -> !task.getTaskStatus().equals(TaskStatus.COMPLETED)
            ).toList());
            model.addAttribute("completedTasks", user.get().getTasks().stream().filter(
                    task -> task.getTaskStatus().equals(TaskStatus.COMPLETED)
            ).toList());
            model.addAttribute("events", user.get().getCreatedEvents().stream().filter(
                    event -> LocalDateTime.now().isBefore(event.getStartDate()) ||
                            LocalDateTime.now().isEqual(event.getStartDate())
            ).toList());
            model.addAttribute("categories", taskCategoryService.findAll());
        }
    }

    @GetMapping
    public String tasks() {
        return "tasks";
    }

    @GetMapping("/create")
    public String create(Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {

            return "createTask";
        }
        return "redirect:/notfound";
    }

    @PostMapping("/create")
    public String create(@Valid Task task, BindingResult bindingResult, Authentication authentication, Model model) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isPresent()) {
            if (bindingResult.hasErrors()) {
                return "createTask";
            }
            if (task.getDue().isBefore(task.getEvent().getStartDate()) ||
                    task.getDue().isAfter(task.getEvent().getEndDate())) {
                model.addAttribute("dateError", "Due date should be between chosen event dates");
                return "createTask";
            }
            task.setUser(user.get());
            taskService.save(task);
            return "redirect:/tasks";
        }
        return "redirect:/notfound";
    }

    @GetMapping("/{id}")
    public String task(@PathVariable String id, Model model, Authentication authentication) {
        Optional<Task> task = taskService.findById(Long.valueOf(id));
        if (task.isPresent()) {
            if (Objects.equals(task.get().getUser().getUsername(), authentication.getName())) {
                model.addAttribute("task", task.get());
                return "task";
            }
        }
        return "redirect:/notfound";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model, Authentication authentication) {
        Optional<Task> task = taskService.findById(Long.valueOf(id));
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (task.isPresent() && user.isPresent()) {
            model.addAttribute("task", task.get());
            model.addAttribute("events", user.get().getCreatedEvents().stream().filter(
                    event -> LocalDateTime.now().isBefore(event.getStartDate()) ||
                            LocalDateTime.now().isEqual(event.getStartDate())
            ).toList());
            model.addAttribute("categories", taskCategoryService.findAll());
            return "updateTask";
        }
        return "redirect:/notfound";
    }

    @PutMapping("/{id}/edit")
    public String edit(@PathVariable String id, @Valid Task t, Authentication authentication) {
        Optional<Task> task = taskService.findById(Long.valueOf(id));
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (task.isPresent() && user.isPresent()) {
            if (Objects.equals(task.get().getUser().getUsername(), user.get().getUsername())) {
                t.setUser(user.get());
                taskService.update(t);
                return "redirect:/tasks";
            }
            return "redirect:/nopermission";
        }
        return "redirect:/notfound";
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable String id, Authentication authentication) {
        Optional<Task> task = taskService.findById(Long.valueOf(id));
        if (task.isPresent()) {
            if (Objects.equals(task.get().getUser().getUsername(), authentication.getName())) {
//                task.get().setUser(null);
//                task.get().setEvent(null);
//                task.get().setCategory(null);
                taskService.delete(task.get());
                return "redirect:/tasks";
            } else {
                return "redirect:/nopermission";
            }
        }
        return "redirect:/notfound";
    }

    @PutMapping("/{id}/complete")
    public String complete(@PathVariable String id, Authentication authentication) {
        Optional<Task> task = taskService.findById(Long.valueOf(id));
        if (task.isPresent()) {
            if (Objects.equals(task.get().getUser().getUsername(), authentication.getName())) {
                taskService.complete(Long.valueOf(id));
                return "redirect:/tasks";
            } else {
                return "redirect:/nopermission";
            }
        }
        return "redirect:/notfound";
    }
}
