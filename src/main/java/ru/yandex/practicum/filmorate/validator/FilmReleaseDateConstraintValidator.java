package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilmReleaseDateConstraintValidator implements ConstraintValidator<FilmReleaseDateConstraint, LocalDate> {
    private static final LocalDate FILM_DEVELOPMENT_DATE = LocalDate.parse("1895-12-28", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return !localDate.isBefore(FILM_DEVELOPMENT_DATE);
    }
}
