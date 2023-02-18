package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film addFilm(Film film) {
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

        return filmStorage.update(film.toBuilder().likes(likes).build());
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        Set<Long> likes = film.getLikes();
        likes.remove(userId);

        return filmStorage.update(film.toBuilder().likes(likes).build());
    }

    public List<Film> getTop(Integer count) {
        if (count == null) count = 10;
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(o -> o.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
