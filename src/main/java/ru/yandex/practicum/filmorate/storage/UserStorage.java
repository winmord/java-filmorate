package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User getById(Integer id);

    User create(User user);

    User delete(Integer id);

    User update(User user);
}
