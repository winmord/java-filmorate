package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmService.getAllFilms();
        log.info("Запрошено {} фильмов", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @Validated @RequestBody Film film) {
        film = filmService.addFilm(film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        film = filmService.updateFilm(film);
        log.info("Фильм обновлён: {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        Film film = filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        Film film = filmService.removeLike(id, userId);
        log.info("Пользователь {} удалил лайк фильму {}", userId, id);
        return film;
    }

    @GetMapping("/popular")
    public Collection<Film> getTop(@RequestParam(required = false, defaultValue = "10") Integer count) {
        Collection<Film> films = filmService.getTop(count);
        log.info("Запрошено топ-{} фильмов", count);
        return films;
    }
}
