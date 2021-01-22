package com.team33.FDMGamification.Validation.Validator;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Validation.Annotation.NullOrNotEqualChallengeID;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class NullOrNotEqualChallengeIDValidator implements ConstraintValidator<NullOrNotEqualChallengeID, Challenge> {

    private int notEqual;

    @Override
    public boolean isValid(Challenge value, ConstraintValidatorContext context) {
        return value == null || value.getId() != notEqual;
    }

    @Override
    public void initialize(NullOrNotEqualChallengeID constraintAnnotation) {
        this.notEqual = constraintAnnotation.notEqual();
    }
}
