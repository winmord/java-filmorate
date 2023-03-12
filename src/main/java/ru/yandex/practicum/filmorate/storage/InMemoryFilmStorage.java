package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Collections;

@Component
public class InMemoryFilmStorage extends AbstractStorage<Film> implements FilmStorage {
    @Override
    protected void validate(Long id) {
        if (notContainsId(id)) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", id));
        }
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        super.create(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        super.update(film.getId(), film);
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        return null;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        return null;
    }

    @Override
    public Collection<Film> getTop(Integer topCount) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Integer> getGenres(Long id) {
        return Collections.emptyList();
    }
}
