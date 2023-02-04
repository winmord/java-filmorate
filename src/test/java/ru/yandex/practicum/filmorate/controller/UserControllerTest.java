package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user.toBuilder().id(1).build())));

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
                        )).hasCauseInstanceOf(UserDoesNotExistException.class).hasMessageContaining("Не существует пользователя с id=null");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user.toBuilder().id(1).name("new name").build()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user.toBuilder().id(1).name("new name").build())));
    }
}