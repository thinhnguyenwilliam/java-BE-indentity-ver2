package com.dev.identity_service.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {DobConstraintValidator.class}
)
public @interface DobConstraint
{
    String message() default "Invalid date of birth: User must be at least {min} years old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min(); // Minimum age

}
