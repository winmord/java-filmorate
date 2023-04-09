package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage extends AbstractStorage<User> implements UserStorage {
    private final Map<Long, List<Long>> friends = new HashMap<>();

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
    public Optional<User> addFriend(Long userId, Long friendId) {
        List<Long> userFriends = friends.getOrDefault(userId, new ArrayList<>());
        userFriends.add(friendId);

        friends.put(userId, userFriends);

        return getById(userId);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        validate(id);

        Optional<User> user = super.getById(id);

        return user.<Collection<User>>map(user1 -> friends.get(user1.getId()).stream()
                        .map(value -> super.getById(value).get())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<User> deleteFriend(Long userId, Long friendId) {
        validate(userId);
        validate(friendId);

        Optional<User> user = super.getById(userId);

        user.ifPresent(value -> friends.get(value.getId()).remove(friendId));

        return user;
    }

    @Override
    public Optional<User> confirmFriendship(Long userId, Long friendId) {
        return super.getById(userId);
    }
}
