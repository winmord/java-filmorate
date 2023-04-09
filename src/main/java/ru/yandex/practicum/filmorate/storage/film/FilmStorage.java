package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAll();

    Optional<Film> getById(Long id);

    Film create(Film film);

    Optional<Film> delete(Long id);

    Film update(Film film);

    Optional<Film> addLike(Long filmId, Long userId);

    Optional<Film> removeLike(Long filmId, Long userId);

    Collection<Film> getTop(Integer topCount);

    Collection<Integer> getGenres(Long id);

    Collection<Long> getLikes(Long id);
}
