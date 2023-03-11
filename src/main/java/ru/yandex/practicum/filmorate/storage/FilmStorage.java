package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAll();

    Film getById(Long id);

    Film create(Film film);

    Film delete(Long id);

    Film update(Film film);

    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    Collection<Film> getTop(Integer topCount);
}
