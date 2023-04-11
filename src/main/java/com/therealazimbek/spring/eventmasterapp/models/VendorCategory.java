package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class VendorCategory {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message="Title is required and it should be unique")
    @Size(min=5, message="Title must be at least 5 characters long")
    private String title;

    private String details;

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private List<Vendor> vendors = new ArrayList<>();
}
