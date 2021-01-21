package com.team33.FDMGamification.Validation.Validator;

import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Validation.Annotation.NullOrNotEqualQuestionID;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class NullOrNotEqualQuestionIDValidator implements ConstraintValidator<NullOrNotEqualQuestionID, Question> {

    private int notEqual;

    @Override
    public boolean isValid(Question value, ConstraintValidatorContext context) {
        return value == null || value.getQuestionId() != notEqual;
    }

    @Override
    public void initialize(NullOrNotEqualQuestionID constraintAnnotation) {
        this.notEqual = constraintAnnotation.notEqual();
    }
}