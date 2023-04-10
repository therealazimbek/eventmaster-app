package com.therealazimbek.spring.eventmasterapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

@Data
@Entity
public class UserPaymentCard {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreditCardNumber(message="Not a valid credit card number")
    private String cardNumber;

    @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([2-9][0-9])$",
            message="Must be formatted MM/YY")
    private String cardExpiration;

    @Digits(integer=3, fraction=0, message="Invalid CVV")
    private String cardCvv;
}
