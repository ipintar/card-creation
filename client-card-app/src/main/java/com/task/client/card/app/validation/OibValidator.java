package com.task.client.card.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for validating Croatian OIB (Personal Identification Number) fields.
 * This annotation can be applied to fields or method parameters, and it uses the {@link OibValidatorImpl}
 * class to perform the actual validation logic, which checks if the OIB is 11 digits long and follows the Mod 11 algorithm.
 */
@Constraint(validatedBy = OibValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface OibValidator {

    /**
     * Error message to be returned if the OIB validation fails.
     *
     * @return the error message
     */
    String message() default "Invalid OIB";

    /**
     * Allows specification of validation groups.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Allows specification of payload objects that can be attached to a validation constraint.
     *
     * @return the payload classes
     */
    Class<? extends Payload>[] payload() default {};
}
