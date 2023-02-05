package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int autoGeneratingId = 0;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрошено {} фильмов", films.size());
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @Validated @RequestBody Film film) {
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
