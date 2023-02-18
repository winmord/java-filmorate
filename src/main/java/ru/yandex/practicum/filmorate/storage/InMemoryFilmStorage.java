package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long autoGeneratingId = 0L;

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getById(Long id) {
        if (!films.containsKey(id)) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", id));
        }
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        film.setId(++autoGeneratingId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Long id) {
        return films.remove(id);
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", film.getId()));
        }

        films.put(film.getId(), film);
        return film;
    }
}
