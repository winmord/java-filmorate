package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final Map<Long, User> users = new HashMap<>();
    private Long autoGeneratingId = 0L;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрошено {} пользователей", users.size());
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        user.setId(++autoGeneratingId);

        checkIfUserNameIsEmpty(user);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new UserDoesNotExistException("Не существует пользователя с id=" + user.getId());
        }

        checkIfUserNameIsEmpty(user);
        users.put(user.getId(), user);
        log.info("Пользователь обновлён: {}", user);
        return user;
    }

    private void checkIfUserNameIsEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя пустое. Вместо имени будет использоваться логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
