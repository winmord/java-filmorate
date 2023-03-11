package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.WhitespacesConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    Long id;

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

    @JsonIgnore
    Set<Long> friends = new HashSet<>();

    Instant createdAt;
    Instant deletedAt;
}
