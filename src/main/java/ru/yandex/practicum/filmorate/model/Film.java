package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Film {
    Integer id;
    String name;
    String description;
    Instant releaseDate;
    Integer duration;
}
