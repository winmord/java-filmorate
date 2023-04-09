package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreDbStorage genreDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAll();

        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()).stream().map(genreDbStorage::getById).collect(Collectors.toSet()));
        }

        return films;
    }

    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film getFilmById(Long id) {
        Optional<Film> film = filmStorage.getById(id);

        if (film.isEmpty()) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", id));
        }

        film.get().setGenres(filmStorage.getGenres(id).stream().map(genreDbStorage::getById).collect(Collectors.toSet()));
        return film.get();
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId());

        Set<Genre> genres = film.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return filmStorage.update(film).toBuilder().genres(genres).build();
    }

    public Film addLike(Long filmId, Long userId) {
        if (userStorage.getById(userId).isEmpty()) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", userId));
        }

        Optional<Film> film = filmStorage.addLike(filmId, userId);

        if (film.isEmpty()) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", filmId));
        }

        return film.get();
    }

    public Film removeLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.removeLike(filmId, userId);

        if (userStorage.getById(userId).isEmpty()) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", userId));
        }

        if (film.isEmpty()) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", filmId));
        }

        return film.get();
    }

    public Collection<Film> getTop(Integer count) {
        Collection<Film> films = filmStorage.getTop(count);

        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()).stream().map(genreDbStorage::getById).collect(Collectors.toSet()));
        }

        return films;
    }
}
