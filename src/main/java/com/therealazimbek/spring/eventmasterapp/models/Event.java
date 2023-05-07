package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message="Name is required")
    @Size(min=10, message="Name must be at least 10 characters long")
    private String name;

    @DecimalMax(value = "1000")
    @Digits(fraction = 0, message = "Enter only numbers", integer = 3)
    private BigDecimal price;

    @NotNull(message = "Budget is required")
    @DecimalMax(value = "100000")
    @Digits(fraction = 0, message = "Enter only numbers", integer = 5)
    private BigDecimal budget;

    @NotNull(message = "Enter maximum number of guests")
    @Min(value = 10, message = "Minimum 10 guests are allowed")
    @Digits(fraction = 0, message = "Enter only numbers", integer = 3)
    private BigDecimal maxGuests;

    @NotNull(message = "Is the event private?")
    private Boolean isPrivate;

    @NotBlank(message="Main topic is required")
    @Size(min=5, message="Topic must be at least 5 characters long")
    private String topic;

    @NotNull(message = "Specify start date time")
    private LocalDateTime startDate;

    @NotNull(message = "Specify end date time")
    private LocalDateTime endDate;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(name = "event_vendor",
            joinColumns = @JoinColumn(name = "vendor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"))
    private List<Vendor> vendors;

    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<UserEvent> eventUsers = new ArrayList<>();
}
