package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
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
        film.setCreatedAt(Instant.now());
        return filmStorage.create(film);
    }

    public Film getFilmById(Long id) {
        try {
            Film film = filmStorage.getById(id);
            film.setGenres(filmStorage.getGenres(id).stream().map(genreDbStorage::getById).collect(Collectors.toSet()));

            return film;
        } catch (Exception e) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", id));
        }
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", film.getId()));
        }

        Set<Genre> genres = film.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return filmStorage.update(film).toBuilder().genres(genres).build();
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
        Collection<Film> films = filmStorage.getTop(count);

        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()).stream().map(genreDbStorage::getById).collect(Collectors.toSet()));
        }

        return films;
    }
}
