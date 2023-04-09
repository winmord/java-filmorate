package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.impl.GenreDbStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<Genre> getAllGenres() {
        return genreDbStorage.getAll();
    }

    public Genre getGenreById(Integer id) {
        try {
            return genreDbStorage.getById(id);
        } catch (Exception e) {
            throw new GenreDoesNotExistException(String.format("Жанр %s не существует", id));
        }
    }
}
