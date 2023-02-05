package ru.yandex.practicum.filmorate.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WhitespacesConstraintValidator implements ConstraintValidator<WhitespacesConstraint, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.contains(" ");
    }
}
