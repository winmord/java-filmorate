package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

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
    public Optional<Film> addLike(Long filmId, Long userId) {
        validate(filmId);

        Optional<Film> film = super.getById(filmId);
        film.ifPresent(value -> value.getLikes().add(userId));

        return film;
    }

    @Override
    public Optional<Film> removeLike(Long filmId, Long userId) {
        validate(filmId);

        Optional<Film> film = super.getById(filmId);
        film.ifPresent(value -> value.getLikes().remove(userId));

        return film;
    }

    @Override
    public Collection<Film> getTop(Integer topCount) {
        return super.getAll().stream()
                .sorted(reverseOrder(Comparator.comparingInt(o -> o.getLikes().size())))
                .limit(topCount)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Integer> getGenres(Long id) {
        validate(id);

        Optional<Film> film = super.getById(id);

        return film.<Collection<Integer>>map(value -> value.
                        getGenres().stream()
                        .map(Genre::getId)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public Collection<Long> getLikes(Long id) {
        validate(id);

        Optional<Film> film = super.getById(id);

        if (film.isPresent()) {
            return film.get().getLikes();
        }

        return Collections.emptyList();
    }
}
