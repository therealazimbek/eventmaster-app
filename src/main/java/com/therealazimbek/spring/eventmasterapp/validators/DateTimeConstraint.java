package com.therealazimbek.spring.eventmasterapp.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateTimeValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeConstraint {
    String message() default "Invalid date time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
