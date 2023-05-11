package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Venue {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 10, message = "Name must be at least 10 characters long")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Address is required")
    @Size(min = 10, message = "Address must be at least 10 characters long")
    private String address;

    @NotNull(message = "Enter maximum number of max capacity")
    @Min(value = 30, message = "Minimum 30 is allowed")
    @Digits(fraction = 0, message = "Enter only numbers", integer = 4)
    private BigDecimal capacity;

    @NotNull(message = "Specify price per event")
    @DecimalMax(value = "1000")
    @Digits(fraction = 0, message = "Enter only numbers", integer = 3)
    private BigDecimal price;

    @NotBlank(message = "Phone is required and it should be unique")
    @Column(unique = true)
    @Pattern(regexp = "^\\+?77(\\d{9})$", message = "Must be formatted +77XXXXXXXXX")
    private String phone;

    @NotBlank(message = "Email is required and it should be unique")
    @Column(unique = true)
    @Email
    private String email;

    @NotBlank(message = "Details is required")
    @Size(max = 3000, message = "Topic must be at most 3000 characters long")
    private String details;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.PERSIST)
    private List<Event> events = new ArrayList<>();
}
