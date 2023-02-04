package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class User {
    Integer id;
    String email;
    String login;
    String name;
    Instant birthday;
}
