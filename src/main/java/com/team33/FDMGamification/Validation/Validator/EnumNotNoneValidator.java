package com.team33.FDMGamification.Validation.Validator;

import com.team33.FDMGamification.Validation.Annotation.EnumNotEquals;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class EnumNotNoneValidator implements ConstraintValidator<EnumNotEquals, Enum<?>> {

    String notEqual = null;

    @Override
    public void initialize(EnumNotEquals constraintAnnotation) {
        notEqual = constraintAnnotation.notEqual();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        return !value.name().equals(notEqual);
    }
}
