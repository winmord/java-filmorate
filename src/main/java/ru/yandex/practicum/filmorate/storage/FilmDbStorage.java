package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        String sqlQuery = "SELECT * " +
                "FROM film " +
                "INNER JOIN mpa_rating ON film.mpa_rating_id = mpa_rating.mpa_rating_id " +
                "WHERE film.deleted_at IS NULL";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getById(Long id) {
        String sqlQuery = "SELECT * " +
                "FROM film " +
                "INNER JOIN mpa_rating ON film.mpa_rating_id = mpa_rating.mpa_rating_id " +
                "WHERE film.film_id = ? " +
                "AND film.deleted_at IS NULL";
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
        if (film.getGenres() == null) return;

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_genre")
                .usingColumns("film_id", "genre_id");

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
        String sqlQuery = "UPDATE film " +
                "SET film.name = ?, film.description = ?, film.release_date = ?, film.duration = ?, film.mpa_rating_id = ? " +
                "WHERE film.film_id = ? AND film.deleted_at IS NULL";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        deleteFilmGenreReference(film.getId());
        createFilmGenreReference(film);

        return film;
    }

    public Film addLike(Long filmId, Long userId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_like")
                .usingColumns("film_id", "user_id", "created_at");


        simpleJdbcInsert.execute(
                Map.of("film_id", filmId,
                        "user_id", userId,
                        "created_at", Instant.now()
                )
        );

        return getById(filmId);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        String sqlQuery = "UPDATE film_like " +
                "SET film_like.deleted_at = ? " +
                "WHERE film_like.film_id = ? " +
                "AND film_like.user_id = ? " +
                "AND film_like.deleted_at IS NULL";

        jdbcTemplate.update(sqlQuery,
                Instant.now(),
                filmId,
                userId);

        return getById(filmId);
    }

    @Override
    public Collection<Film> getTop(Integer topCount) {
        String sqlQuery = "SELECT * FROM " +
                "(SELECT film_like.film_id " +
                "FROM film_like " +
                "WHERE film_like.deleted_at IS NULL " +
                "GROUP BY film_like.film_id " +
                "ORDER BY count(film_like.film_id) DESC) AS top " +
                "RIGHT JOIN film ON top.film_id = film.film_id " +
                "INNER JOIN mpa_rating ON film.mpa_rating_id = mpa_rating.mpa_rating_id " +
                "WHERE film.deleted_at IS NULL " +
                "ORDER BY top.FILM_ID DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), topCount);
    }

    @Override
    public Collection<Integer> getGenres(Long id) {
        String sqlQuery = "SELECT film_genre.genre_id FROM film_genre WHERE film_genre.film_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("genre_id"), id);
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_rating_id", film.getMpa().getId());
        values.put("created_at", film.getCreatedAt());
        values.put("deleted_at", film.getDeletedAt());
        return values;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("film.film_id");
        String name = rs.getString("film.name");
        String description = rs.getString("film.description");
        LocalDate releaseDate = rs.getDate("film.release_date").toLocalDate();
        Integer duration = rs.getInt("film.duration");
        Integer mpaRatingId = rs.getInt("mpa_rating_id");
        String mpaRatingName = rs.getString("mpa_rating.name");
        Instant createdAt = rs.getTimestamp("film.created_at").toInstant();
        Timestamp deletedAt = rs.getTimestamp("film.deleted_at");

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(new Mpa(mpaRatingId, mpaRatingName))
                .createdAt(createdAt)
                .deletedAt(deletedAt == null ? null : deletedAt.toInstant())
                .build();
    }
}
