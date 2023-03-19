package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS friendship CASCADE");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users" +
                "(" +
                "    user_id    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "    email      varchar   NOT NULL," +
                "    login      varchar   NOT NULL," +
                "    name       varchar   NOT NULL," +
                "    birthday   date      NOT NULL," +
                "    created_at timestamp NOT NULL," +
                "    deleted_at timestamp" +
                ");");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS friendship" +
                "(" +
                "    user_id      INTEGER REFERENCES users (user_id)," +
                "    friend_id    INTEGER REFERENCES users (user_id)," +
                "    created_at   timestamp NOT NULL," +
                "    confirmed_at timestamp," +
                "    deleted_at   timestamp," +
                "    PRIMARY KEY (user_id, friend_id)" +
                ");");
    }

    @Test
    void getAll() {
        User firstUser = User.builder()
                .login("login1")
                .name("name1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .friends(new HashSet<>())
                .build();

        User secondUser = User.builder()
                .login("login2")
                .name("name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1956, Month.APRIL, 2))
                .friends(new HashSet<>())
                .build();

        userStorage.create(firstUser);
        userStorage.create(secondUser);

        List<User> users = new ArrayList<>(userStorage.getAll());
        assertEquals(2, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals(2L, users.get(1).getId());
    }

    @Test
    void getById() {
        User user = User.builder()
                .login("login")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .friends(new HashSet<>())
                .build();

        userStorage.create(user);

        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(usr ->
                        assertThat(usr).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void create() {
        User user = User.builder()
                .login("login")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .friends(new HashSet<>())
                .build();

        userStorage.create(user);

        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(usr ->
                        assertThat(usr).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void delete() {
        User user = User.builder()
                .login("login")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .friends(new HashSet<>())
                .build();

        userStorage.create(user);

        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(usr ->
                        assertThat(usr).hasFieldOrPropertyWithValue("id", 1L)
                );

        userStorage.delete(1L);

        assertThat(userStorage.getById(1L))
                .isEmpty();
    }

    @Test
    void update() {
        User user = User.builder()
                .login("login")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .friends(new HashSet<>())
                .build();

        userStorage.create(user);

        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(usr ->
                        assertThat(usr).hasFieldOrPropertyWithValue("id", 1L)
                );

        userStorage.update(user.toBuilder().name("new name").build());

        userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(usr ->
                        assertThat(usr).hasFieldOrPropertyWithValue("name", "new name")
                );
    }

    @Test
    void getFriends() {
        User user = User.builder()
                .login("login1")
                .name("name1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .friends(new HashSet<>())
                .build();

        User friend = User.builder()
                .login("login2")
                .name("name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1956, Month.APRIL, 2))
                .friends(new HashSet<>())
                .build();

        userStorage.create(user);
        userStorage.create(friend);
        userStorage.update(user.toBuilder().friends(new HashSet<>(List.of(2L))).build());
        List<User> friends = new ArrayList<>(userStorage.getFriends(1L));

        assertEquals(1, friends.size());
        assertEquals(2L, friends.get(0).getId());
    }

    @Test
    void deleteFriend() {
        User user = User.builder()
                .login("login1")
                .name("name1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .friends(new HashSet<>())
                .build();

        User friend = User.builder()
                .login("login2")
                .name("name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1956, Month.APRIL, 2))
                .friends(new HashSet<>())
                .build();

        userStorage.create(user);
        userStorage.create(friend);
        userStorage.update(user.toBuilder().friends(new HashSet<>(List.of(2L))).build());
        List<User> friends = new ArrayList<>(userStorage.getFriends(1L));

        assertEquals(1, friends.size());
        assertEquals(2L, friends.get(0).getId());

        userStorage.deleteFriend(1L, 2L);

        friends = new ArrayList<>(userStorage.getFriends(1L));

        assertTrue(friends.isEmpty());
    }
}