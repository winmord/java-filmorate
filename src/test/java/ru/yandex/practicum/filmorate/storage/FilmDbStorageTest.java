package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS film CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS film_like CASCADE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS film" +
                "(" +
                "    film_id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "    name          varchar   NOT NULL," +
                "    description   varchar   NOT NULL," +
                "    release_date  date      NOT NULL," +
                "    duration      INTEGER   NOT NULL," +
                "    mpa_rating_id INTEGER REFERENCES mpa_rating (mpa_rating_id)," +
                "    created_at    timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "    deleted_at    timestamp" +
                ");");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users" +
                "(" +
                "    user_id    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "    email      varchar   NOT NULL," +
                "    login      varchar   NOT NULL," +
                "    name       varchar   NOT NULL," +
                "    birthday   date      NOT NULL," +
                "    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "    deleted_at timestamp" +
                ");");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS film_like" +
                "(" +
                "    film_id    INTEGER REFERENCES film (film_id)," +
                "    user_id    INTEGER REFERENCES users (user_id)," +
                "    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "    deleted_at timestamp," +
                "    PRIMARY KEY (film_id, user_id)" +
                ");");
    }

    @Test
    void getAll() {
        Film firstFilm = Film.builder()
                .name("film1")
                .description("description1")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .mpa(new Mpa(1, null))
                .build();

        Film secondFilm = Film.builder()
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.of(1926, Month.AUGUST, 20))
                .duration(101)
                .mpa(new Mpa(1, null))
                .build();

        filmStorage.create(firstFilm);
        filmStorage.create(secondFilm);

        List<Film> films = new ArrayList<>(filmStorage.getAll());
        assertEquals(2, films.size());
        assertEquals(1L, films.get(0).getId());
        assertEquals(2L, films.get(1).getId());
    }

    @Test
    void getById() {
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .likes(new HashSet<>())
                .mpa(new Mpa(1, null))
                .build();

        filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.getById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(flm ->
                        assertThat(flm).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void create() {
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .likes(new HashSet<>())
                .mpa(new Mpa(1, null))
                .build();

        filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.getById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(flm ->
                        assertThat(flm).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void delete() {
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .likes(new HashSet<>())
                .mpa(new Mpa(1, null))
                .build();

        filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.getById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(flm ->
                        assertThat(flm).hasFieldOrPropertyWithValue("id", 1L)
                );

        filmStorage.delete(1L);

        assertThat(filmStorage.getById(1L))
                .isEmpty();
    }

    @Test
    void update() {
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .likes(new HashSet<>())
                .mpa(new Mpa(1, null))
                .build();

        filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.getById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(usr ->
                        assertThat(usr).hasFieldOrPropertyWithValue("id", 1L)
                );

        filmStorage.update(film.toBuilder().name("new film name").build());

        filmOptional = filmStorage.getById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(flm ->
                        assertThat(flm).hasFieldOrPropertyWithValue("name", "new film name")
                );
    }

    @Test
    void addLike() {
        Long filmId = 1L;
        Long userId = 1L;

        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .likes(new HashSet<>())
                .mpa(new Mpa(1, null))
                .build();

        User user = User.builder()
                .login("login")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        filmStorage.create(film);
        userStorage.create(user);
        filmStorage.addLike(filmId, userId);

        List<Long> likes = new ArrayList<>(filmStorage.getLikes(filmId));

        assertEquals(List.of(userId), likes);
    }

    @Test
    void removeLike() {
        Long filmId = 1L;
        Long userId = 1L;

        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .likes(new HashSet<>())
                .mpa(new Mpa(1, null))
                .build();

        User user = User.builder()
                .login("login")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        filmStorage.create(film);
        userStorage.create(user);
        filmStorage.addLike(filmId, userId);

        List<Long> likes = new ArrayList<>(filmStorage.getLikes(filmId));
        assertEquals(List.of(userId), likes);

        filmStorage.removeLike(filmId, userId);

        likes = new ArrayList<>(filmStorage.getLikes(filmId));
        assertTrue(likes.isEmpty());
    }

    @Test
    void getTop() {
        Long firstFilmId = 1L;
        Long secondFilmId = 2L;
        Long firstUserId = 1L;
        Long secondUserId = 2L;

        Film firstFilm = Film.builder()
                .name("film1")
                .description("description1")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .build();

        Film secondFilm = Film.builder()
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.of(1926, Month.AUGUST, 20))
                .duration(101)
                .mpa(new Mpa(1, "G"))
                .build();

        filmStorage.create(firstFilm);
        filmStorage.create(secondFilm);

        User firstUser = User.builder()
                .login("login1")
                .name("name1")
                .email("mail1@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        User secondUser = User.builder()
                .login("login2")
                .name("name2")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1956, Month.APRIL, 2))
                .build();

        userStorage.create(firstUser);
        userStorage.create(secondUser);
        filmStorage.addLike(secondFilmId, firstUserId);

        List<Film> topFilms = new ArrayList<>(filmStorage.getTop(1));
        assertEquals(List.of(secondFilm), topFilms);

        topFilms = new ArrayList<>(filmStorage.getTop(2));
        assertEquals(List.of(secondFilm, firstFilm), topFilms);

        filmStorage.addLike(firstFilmId, firstUserId);
        filmStorage.addLike(firstFilmId, secondUserId);

        topFilms = new ArrayList<>(filmStorage.getTop(2));
        assertEquals(List.of(firstFilm, secondFilm), topFilms);
    }

    @Test
    void getGenres() {
        Long filmId = 1L;
        Integer genreId = 1;

        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .likes(new HashSet<>())
                .mpa(new Mpa(1, null))
                .genres(Set.of(new Genre(genreId, null)))
                .build();

        filmStorage.create(film);

        List<Integer> genres = new ArrayList<>(filmStorage.getGenres(filmId));
        assertEquals(List.of(genreId), genres);
    }
}