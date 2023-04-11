package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message="Name is required")
    @Size(min=10, message="Name must be at least 10 characters long")
    private String title;

    @NotBlank(message="Description is required")
    @Size(min=10, message="Description must be at least 10 characters long")
    private String description;

    @NotBlank(message = "Specify due date")
    private LocalDateTime due;

    private TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TaskCategory category;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
