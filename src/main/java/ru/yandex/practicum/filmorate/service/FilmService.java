package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, MpaDbStorage mpaDbStorage) {
        this.filmStorage = filmStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAll();

        for (Film film : films) {
            film.setMpa(mpaDbStorage.getById(film.getMpaRatingId()));
            film.setGenres(new HashSet<>());
        }

        return films;
    }

    public Film addFilm(Film film) {
        film.setCreatedAt(Instant.now());
        return filmStorage.create(film);
    }

    public Film getFilmById(Long id) {
        try {

            Film film = filmStorage.getById(id);
            film.setMpa(mpaDbStorage.getById(film.getMpaRatingId()));
            film.setGenres(new HashSet<>());

            return film;
        } catch (Exception e) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", id));
        }
    }

    public Film updateFilm(Film film) {
        try {
            return filmStorage.update(film);
        } catch (Exception e) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", film.getId()));
        }
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        Set<Long> likes = film.getLikes();
        likes.add(userId);

        return filmStorage.update(film);
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        Set<Long> likes = film.getLikes();

        if (!likes.contains(userId)) {
            throw new UserDoesNotExistException("Не существует лайка от пользователя " + userId);
        }

        likes.remove(userId);

        return filmStorage.update(film);
    }

    public List<Film> getTop(Integer count) {
        return getAllFilms().stream()
                .sorted(reverseOrder(Comparator.comparingInt(o -> o.getLikes().size())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
