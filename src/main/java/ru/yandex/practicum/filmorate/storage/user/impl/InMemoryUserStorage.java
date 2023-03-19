package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Component
public class InMemoryUserStorage extends AbstractStorage<User> implements UserStorage {
    @Override
    public void validate(Long id) {
        if (notContainsId(id)) {
            throw new UserDoesNotExistException(String.format("Пользователь %s не существует", id));
        }
    }

    @Override
    protected void fix(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        super.create(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        super.update(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getFriends(Long id) {
        return null;
    }

    @Override
    public Optional<User> deleteFriend(Long userId, Long friendId) {
        return null;
    }
}
