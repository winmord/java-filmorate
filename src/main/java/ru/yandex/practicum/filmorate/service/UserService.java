package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setCreatedAt(Instant.now());
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", user.getId()));
        }

        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        Optional<User> user = userStorage.getById(id);
        if (user.isEmpty()) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", id));
        }

        return user.get();
    }

    public User addFriend(Long userId, Long friendId) {
        try {
            User user = getUserById(userId);

            Set<Long> userFriends = user.getFriends();
            userFriends.add(friendId);

            return userStorage.update(user);
        } catch (Exception e) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", friendId));
        }
    }

    public User deleteFriend(Long userId, Long friendId) {
        Optional<User> user = userStorage.deleteFriend(userId, friendId);
        if (user.isEmpty()) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", friendId));
        }

        return user.get();
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        Set<User> userFriends = new HashSet<>(userStorage.getFriends(userId));
        Set<User> friendFriends = new HashSet<>(userStorage.getFriends(friendId));

        userFriends.retainAll(friendFriends);

        return new ArrayList<>(userFriends);
    }

    public Collection<User> getAllFriends(Long userId) {
        return userStorage.getFriends(userId);
    }
}
