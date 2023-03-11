package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User getById(Long id);

    User create(User user);

    User delete(Long id);

    User update(User user);

    Collection<User> getFriends(Long id);

    User deleteFriend(Long userId, Long friendId);
}
