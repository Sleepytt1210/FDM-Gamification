package com.team33.FDMGamification.Validation.Annotation;

import com.team33.FDMGamification.Validation.Validator.NullOrNotEqualChallengeIDValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( {ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = NullOrNotEqualChallengeIDValidator.class)
public @interface NullOrNotEqualChallengeID {
    int notEqual() default -1;
    String message() default "Must not be blank or equal to -1";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}
