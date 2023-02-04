package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.WhitespacesConstraint;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    Integer id;

    @NotNull
    @NotBlank
    @Email
    String email;

    @NotNull
    @NotBlank
    @WhitespacesConstraint
    String login;

    String name;

    @NotNull
    @PastOrPresent
    LocalDate birthday;
}
