package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT * FROM film WHERE film.deleted_at IS NULL";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getById(Long id) {
        return null;
    }

    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public Film delete(Long id) {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return new Film();
    }
}
