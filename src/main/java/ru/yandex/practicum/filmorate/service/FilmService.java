package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film addFilm(Film film) {
        film.setCreatedAt(Instant.now());
        return filmStorage.create(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
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
        return filmStorage.getAll().stream()
                .sorted(reverseOrder(Comparator.comparingInt(o -> o.getLikes().size())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
