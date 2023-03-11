package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        MpaDbStorage mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        FilmService filmService = new FilmService(filmStorage, userStorage, mpaDbStorage);
        mockMvc = MockMvcBuilders.standaloneSetup(new FilmController(filmService)).build();
    }

    @Test
    void getAllFilms() throws Exception {
        mockMvc.perform(
                        get("/films")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        Film firstFilm = Film.builder()
                .name("film1")
                .description("description1")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .build();

        Film secondFilm = Film.builder()
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.of(1926, Month.AUGUST, 20))
                .duration(101)
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(firstFilm))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(secondFilm))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/films")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("film1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("film2"));
    }

    @Test
    void addFilm() throws Exception {
        LocalDate releaseDate = LocalDate.of(1946, Month.AUGUST, 20);
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(releaseDate)
                .duration(100)
                .likes(new HashSet<>())
                .build();

        MvcResult result = mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(objectMapper.readValue(result.getResponse().getContentAsString(), Film.class), film.toBuilder().id(1L).build());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film.toBuilder().name("").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film.toBuilder().description("1234567890".repeat(22)).build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film.toBuilder().releaseDate(LocalDate.of(1346, Month.AUGUST, 20)).build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film.toBuilder().duration(0).build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film.toBuilder().duration(-1).build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilm() throws Exception {
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1946, Month.AUGUST, 20))
                .duration(100)
                .likes(new HashSet<>())
                .build();

        Assertions
                .assertThatThrownBy(
                        () -> mockMvc.perform(
                                put("/films")
                                        .content(objectMapper.writeValueAsString(film))
                                        .contentType(MediaType.APPLICATION_JSON)
                        )).hasCauseInstanceOf(FilmDoesNotExistException.class).hasMessageContaining("Фильм null не существует");

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film.toBuilder().id(1L).name("new name").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(objectMapper.readValue(result.getResponse().getContentAsString(), Film.class), film.toBuilder().id(1L).name("new name").build());
    }
}