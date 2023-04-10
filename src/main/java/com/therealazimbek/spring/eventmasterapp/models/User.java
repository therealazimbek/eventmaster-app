package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message="Username is required and it should be unique")
    @Size(min=5, message="Username must be at least 5 characters long")
    @Column(unique = true)
    private String username;

    @NotBlank(message="Username is required and it should be unique")
    @Size(min=2, message="Firstname must be at least 2 characters long")
    private String firstName;

    @NotBlank(message="Username is required and it should be unique")
    @Size(min=2, message="Lastname must be at least 2 characters long")
    private String lastName;

    @NotBlank(message="Phone is required and it should be unique")
    @Column(unique = true)
    @Pattern(regexp = "^\\+?77(\\d{9})$", message = "Must be formatted +77XXXXXXXXX")
    private String phone;

    @NotBlank(message="Email is required and it should be unique")
    @Column(unique = true)
    @Email
    private String email;

    @NotBlank(message="City is required")
    private String city;

    private String additionalInfo;

    @NotBlank(message="Birthday is required")
    private LocalDate birthday;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Event> createdEvents = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserEvent> userEvents = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserPaymentCard> paymentCards = new ArrayList<>();
}
