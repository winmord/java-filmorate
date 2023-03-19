package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

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
    public Optional<User> getById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE users.user_id = ? AND users.deleted_at IS NULL";

        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Long userId = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue();
        user.setId(userId);

        createUserFriendReference(user);

        return user;
    }

    @Override
    public Optional<User> delete(Long id) {
        String sqlQuery = "UPDATE users SET users.deleted_at = ? WHERE users.user_id = ? AND users.deleted_at IS NULL";
        Optional<User> user = getById(id);
        jdbcTemplate.update(sqlQuery, Instant.now(), id);

        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users " +
                "SET users.email = ?, users.login = ?, users.name = ?, users.birthday = ? " +
                "WHERE users.user_id = ? AND users.deleted_at IS NULL";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        createUserFriendReference(user);

        return user;
    }

    private void createUserFriendReference(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship")
                .usingColumns("user_id", "friend_id", "created_at");

        for (Long friendId : user.getFriends()) {
            simpleJdbcInsert.execute(
                    Map.of("user_id", user.getId(),
                            "friend_id", friendId,
                            "created_at", Instant.now()
                    )
            );
        }
    }

    public Collection<User> getFriends(Long id) {
        String sqlQuery = "SELECT * FROM users " +
                "WHERE users.user_id IN (SELECT friend_id " +
                "FROM friendship " +
                "WHERE friendship.user_id = " + id.toString() +
                " AND friendship.deleted_at IS NULL) " +
                "AND users.deleted_at IS NULL;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    public Optional<User> deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "UPDATE friendship " +
                "SET friendship.deleted_at = ? " +
                "WHERE friendship.user_id = ? AND friendship.friend_id = ? " +
                "AND friendship.deleted_at IS NULL";

        jdbcTemplate.update(sqlQuery,
                Instant.now(),
                userId,
                friendId);

        return getById(userId);
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        values.put("created_at", Instant.now());
        return values;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .friends(new HashSet<>())
                .build();
    }
}
