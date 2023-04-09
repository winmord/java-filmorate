package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Film {
    Long id;

    @NotNull @NotBlank String name;

    @Size(max = 200) String description;

    @NotNull
    @FilmReleaseDateConstraint
    LocalDate releaseDate;

    @Positive Integer duration;

    @JsonIgnore
    Set<Long> likes = new HashSet<>();
    Mpa mpa;
    Set<Genre> genres = new LinkedHashSet<>();
}
