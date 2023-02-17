package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAll();

    Film getById(Integer id);

    Film create(Film film);

    Film delete(Integer id);

    Film update(Film film);
}
