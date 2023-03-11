package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;

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
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "SELECT * FROM users WHERE users.deleted_at IS NULL";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE users.user_id = ? AND users.deleted_at IS NULL";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Long userId = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue();
        user.setId(userId);

        return user;
    }

    @Override
    public User delete(Long id) {
        String sqlQuery = "DELETE FROM users WHERE users.user_id = ? AND users.deleted_at IS NULL";
        User user = getById(id);
        jdbcTemplate.update(sqlQuery, id);

        return user;
    }

    @Override
    public User update(User user) {
        if (getById(user.getId()) == null) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", user.getId()));
        }

        String sqlQuery = "UPDATE users " +
                "SET users.email = ?, users.login = ?, users.name = ?, users.birthday = ? " +
                "WHERE users.user_id = ? AND users.deleted_at IS NULL";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        values.put("created_at", user.getCreatedAt());
        values.put("deleted_at", user.getDeletedAt());
        return values;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        Instant createdAt = rs.getTimestamp("created_at").toInstant();
        Timestamp deletedAt = rs.getTimestamp("deleted_at");

        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .createdAt(createdAt)
                .deletedAt(deletedAt == null ? null : deletedAt.toInstant())
                .build();
    }
}
