package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int autoGeneratingId = 0;
    private static final LocalDate FILM_DEVELOPMENT_DATE = LocalDate.parse("1895-12-28", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(FILM_DEVELOPMENT_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше, чем дата создания кинематографа");
        }

        film.setId(++autoGeneratingId);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new FilmDoesNotExistException("Не существует фильма с id=" + film.getId());
        }

        films.put(film.getId(), film);
        log.info("Фильм обновлён: {}", film);
        return film;
    }
}
