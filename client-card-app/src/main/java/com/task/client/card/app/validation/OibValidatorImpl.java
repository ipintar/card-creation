package com.task.client.card.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * Implementation of the {@link ConstraintValidator} interface to validate Croatian OIB (Personal Identification Number).
 * This class validates that the given OIB is exactly 11 digits long and conforms to the Mod 11 algorithm.
 * It is used as a custom validator for fields annotated with {@link OibValidator}.
 */
public class OibValidatorImpl implements ConstraintValidator<OibValidator, String> {

    /**
     * Checks if the provided OIB is valid.
     * The OIB must be 11 digits long and conform to a valid check using Mod 11 algorithm.
     *
     * @param oib     the OIB value to validate
     * @param context context in which the constraint is evaluated
     * @return {@code true} if the OIB is valid, {@code false} otherwise
     */
    @Override
    public boolean isValid(final String oib, final ConstraintValidatorContext context) {
        if (oib == null || oib.length() != 11) {
            return false;
        }
        return isValidOib(oib);
    }

    /**
     * Performs the actual OIB validation using the Mod 11 algorithm.
     *
     * @param oib the OIB to validate
     * @return {@code true} if the OIB passes the validation, {@code false} otherwise
     */
    private boolean isValidOib(final String oib) {
        final int length = oib.length();
        final int controlNumber = Character.getNumericValue(oib.charAt(length - 1));

        int a = 10;
        for (int i = 0; i < length - 1; i++) {
            final int broj = Character.getNumericValue(oib.charAt(i));
            a = (a + broj) % 10;
            if (a == 0) {
                a = 10;
            }
            a = (a * 2) % 11;
        }

        int calculatedControlDigit = 11 - a;
        if (calculatedControlDigit == 10) {
            calculatedControlDigit = 0;
        }

        return controlNumber == calculatedControlDigit;
    }
}
