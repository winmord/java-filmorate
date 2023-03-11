package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, MpaDbStorage mpaDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(Long filmId, Long userId) {
        try {
            userStorage.getById(userId);
            return filmStorage.removeLike(filmId, userId);
        } catch (Exception e) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", userId));
        }
    }

    public Collection<Film> getTop(Integer count) {
        return filmStorage.getTop(count);
    }
}
