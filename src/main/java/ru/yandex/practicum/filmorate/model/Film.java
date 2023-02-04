package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.Instant;

@Data
public class Film {
    Integer id;

    @NonNull
    String name;

    String description;
    Instant releaseDate;
    Integer duration;
}
