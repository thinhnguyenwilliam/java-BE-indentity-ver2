package com.dev.identity_service.validator;

import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobConstraintValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    private int minAge;

    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        if (dob == null) {
            return true; // `@NotNull` should handle null validation if required
        }

        // Calculate age
        int age = Period.between(dob, LocalDate.now()).getYears();
        return age >= minAge;
    }
}
