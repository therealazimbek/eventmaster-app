package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@RequiredArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "Username is required and it should be unique")
    @Size(min = 5, message = "Username must be at least 5 characters long")
    @Column(unique = true)
    private String username;

    private String password;

    @NotBlank(message = "Username is required and it should be unique")
    @Size(min = 2, message = "Firstname must be at least 2 characters long")
    private String firstName;

    @NotBlank(message = "Username is required and it should be unique")
    @Size(min = 2, message = "Lastname must be at least 2 characters long")
    private String lastName;

    @NotBlank(message = "Phone is required and it should be unique")
    @Column(unique = true)
    @Pattern(regexp = "^\\+?77(\\d{9})$", message = "Must be formatted +77XXXXXXXXX")
    private String phone;

    @NotBlank(message = "Email is required and it should be unique")
    @Column(unique = true)
    @Email
    private String email;

    @NotBlank(message = "City is required")
    private String city;

    private String additionalInfo;

    @NotNull(message = "Birthday is required")
    @Past
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

    public User(String username, String password, String firstname, String lastname,
                String city, String email, String phone, LocalDate birthday) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstname;
        this.lastName = lastname;
        this.city = city;
        this.phone = phone;
        this.birthday = birthday;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
