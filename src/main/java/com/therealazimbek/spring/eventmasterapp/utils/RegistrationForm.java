package com.therealazimbek.spring.eventmasterapp.utils;

import com.therealazimbek.spring.eventmasterapp.models.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Data
public class RegistrationForm {

    @NotBlank(message = "Email is required and it should be unique")
    @Column(unique = true)
    @Email
    private String email;

    @NotBlank(message = "Username is required and it should be unique")
    @Size(min = 5, message = "Username must be at least 5 characters long")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private String password2;

    private boolean passwordsEqual;

    @NotBlank(message = "Username is required and it should be unique")
    @Size(min = 2, message = "Firstname must be at least 2 characters long")
    private String firstname;

    @NotBlank(message = "Username is required and it should be unique")
    @Size(min = 2, message = "Lastname must be at least 2 characters long")
    private String lastname;

    @NotBlank(message = "Phone is required and it should be unique")
    @Column(unique = true)
    @Pattern(regexp = "^\\+?77(\\d{9})$", message = "Must be formatted +77XXXXXXXXX")
    private String phone;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Birthday is required")
    @Past
    private LocalDate birthday;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username, passwordEncoder.encode(password),
                firstname, lastname, city, email, phone, birthday, true);
    }

    @AssertTrue(message = "Passwords should match")
    public boolean isPasswordsEqual() {
        return password != null && password.equals(password2);
    }
}
