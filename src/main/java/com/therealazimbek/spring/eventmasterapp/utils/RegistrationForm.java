package com.therealazimbek.spring.eventmasterapp.utils;

import com.therealazimbek.spring.eventmasterapp.models.User;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Data
public class RegistrationForm {

    private String email;
    private String username;
    private String password;
    private String password2;
    private String firstname;
    private String lastname;
    private String phone;
    private String city;
    private LocalDate birthday;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username, passwordEncoder.encode(password),
                firstname, lastname, city, email, phone, birthday);
    }
}
