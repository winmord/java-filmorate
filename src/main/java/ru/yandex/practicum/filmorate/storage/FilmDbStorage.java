package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAll() {
        String sqlQuery = "SELECT * FROM film WHERE film.deleted_at IS NULL";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getById(Long id) {
        String sqlQuery = "SELECT * FROM film WHERE film.film_id = ? AND film.deleted_at IS NULL";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");

        Long filmId = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).longValue();
        film.setId(filmId);

        createFilmGenreReference(film);

        return film;
    }

    private void createFilmGenreReference(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_genre")
                .usingColumns("film_id");

        for (Genre genre : film.getGenres()) {
            simpleJdbcInsert.execute(Map.of("film_id", film.getId().toString(), "genre_id", genre.getId()));
        }
    }

    private void deleteFilmGenreReference(Long id) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_genre.film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film delete(Long id) {
        String sqlQuery = "DELETE FROM film WHERE film.film_id = ? AND film.deleted_at IS NULL";
        Film film = getById(id);
        jdbcTemplate.update(sqlQuery, id);

        return film;
    }

    @Override
    public Film update(Film film) {
        if (getById(film.getId()) == null) {
            throw new FilmDoesNotExistException(String.format("Фильм %s не существует", film.getId()));
        }

        String sqlQuery = "UPDATE film " +
                "SET film.name = ?, film.description = ?, film.release_date = ?, film.duration = ?, film.mpa_rating_id = ? " +
                "WHERE film.film_id = ? AND film.deleted_at IS NULL";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRatingId(),
                film.getId());

        deleteFilmGenreReference(film.getId());
        createFilmGenreReference(film);

        return film;
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_rating_id", film.getMpaRatingId());
        values.put("created_at", film.getCreatedAt());
        values.put("deleted_at", film.getDeletedAt());
        return values;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");
        Integer mpaRatingId = rs.getInt("mpa_rating_id");
        Instant createdAt = rs.getTimestamp("created_at").toInstant();
        Timestamp deletedAt = rs.getTimestamp("deleted_at");

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpaRatingId(mpaRatingId)
                .createdAt(createdAt)
                .deletedAt(deletedAt == null ? null : deletedAt.toInstant())
                .build();
    }
}
