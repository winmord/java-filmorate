package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import java.time.LocalDate;

@Data
public class Film {
    Integer id;

    @NotNull
    @NotBlank
    String name;

    @Size(max = 200)
    String description;

    @NotNull
    @FilmReleaseDateConstraint
    LocalDate releaseDate;

    @Positive
    Integer duration;
}
