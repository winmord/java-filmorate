package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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
