package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final JdbcTemplate jdbcTemplate;

    private final UserStorage userStorage;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
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

        UserService userService = new UserService(userStorage);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService)).build();
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

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

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(firstUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(secondUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("name2"));
    }

    @Test
    void addUser() throws Exception {
        User user = User.builder()
                .login("login")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        MvcResult result = mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(objectMapper.readValue(result.getResponse().getContentAsString(), User.class), user.toBuilder().id(1L).build());

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user.toBuilder().email("").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user.toBuilder().email("mailmail.ru").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user.toBuilder().login("").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user.toBuilder().login("l ogin").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user.toBuilder().name("").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getLogin()));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user.toBuilder().birthday(LocalDate.now().plusDays(1)).build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser() throws Exception {
        User user = User.builder()
                .login("login")
                .name("name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, Month.AUGUST, 20))
                .build();

        Assertions
                .assertThatThrownBy(
                        () -> mockMvc.perform(
                                put("/users")
                                        .content(objectMapper.writeValueAsString(user))
                                        .contentType(MediaType.APPLICATION_JSON)
                        )).hasCauseInstanceOf(UserDoesNotExistException.class).hasMessageContaining("Пользователь null не существует");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user.toBuilder().id(1L).name("new name").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(objectMapper.readValue(result.getResponse().getContentAsString(), User.class), user.toBuilder().id(1L).name("new name").build());
    }
}