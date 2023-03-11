package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> getAll() {
        String sqlQuery = "SELECT mpa_rating.mpa_rating_id, mpa_rating.name FROM mpa_rating";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMpa(rs));
    }

    public Mpa getById(Integer id) {
        String sqlQuery = "SELECT mpa_rating.mpa_rating_id, mpa_rating.name FROM mpa_rating WHERE mpa_rating.mpa_rating_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeMpa(rs), id);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Long id = rs.getLong("mpa_rating_id");
        String name = rs.getString("name");

        return Mpa.builder()
                .id(id)
                .name(name)
                .build();
    }
}
