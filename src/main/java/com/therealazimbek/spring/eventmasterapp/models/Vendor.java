package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
public class Vendor {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message="Name is required")
    @Size(min=10, message="Name must be at least 10 characters long")
    private String name;

//    @NotBlank(message="Country is required")
//    @Size(min=3, message="Country must be at least 3 characters long")
    private String country;

    @NotBlank(message="City is required")
    @Size(min=3, message="City must be at least 3 characters long")
    private String city;

    @NotBlank(message="Address is required")
    @Size(min=10, message="Address must be at least 10 characters long")
    private String address;

    @NotNull(message = "Specify price per one person")
    @DecimalMax(value = "1000")
    @Digits(fraction = 0, message = "Enter only numbers", integer = 4)
    private BigDecimal price;

    @NotBlank(message="Phone is required and it should be unique")
    @Column(unique = true)
    @Pattern(regexp = "^\\+?77(\\d{9})$", message = "Must be formatted +77XXXXXXXXX")
    private String phone;

    @NotBlank(message="Email is required and it should be unique")
    @Column(unique = true)
    @Email
    private String email;

    private String details;

    @ManyToMany(mappedBy = "vendors")
    private List<Event> events;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private VendorCategory category;
}
