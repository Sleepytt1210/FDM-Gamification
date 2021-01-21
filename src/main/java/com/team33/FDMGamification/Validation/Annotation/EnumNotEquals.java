package com.team33.FDMGamification.Validation.Annotation;

import com.team33.FDMGamification.Validation.Validator.EnumNotNoneValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumNotNoneValidator.class)
public @interface EnumNotEquals {
    String notEqual() default "NONE";
    String message() default "Please select an option!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}