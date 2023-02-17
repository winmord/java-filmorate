package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int autoGeneratingId = 0;

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getById(Integer id) {
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        film.setId(++autoGeneratingId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Integer id) {
        return films.remove(id);
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }
}
