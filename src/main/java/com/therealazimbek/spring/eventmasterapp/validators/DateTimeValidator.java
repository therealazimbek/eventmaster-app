package com.therealazimbek.spring.eventmasterapp.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class DateTimeValidator implements ConstraintValidator<DateTimeConstraint, LocalDateTime> {

    @Override
    public void initialize(DateTimeConstraint dateTimeConstraint) {
    }

    @Override
    public boolean isValid(LocalDateTime dateTime,
                           ConstraintValidatorContext cxt) {
        if (dateTime == null) return false;
        return LocalDateTime.now().isBefore(dateTime);
    }
}
