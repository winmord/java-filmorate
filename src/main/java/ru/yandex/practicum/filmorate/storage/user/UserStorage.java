package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAll();

    Optional<User> getById(Long id);

    User create(User user);

    Optional<User> delete(Long id);

    User update(User user);

    Optional<User> addFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long id);

    Optional<User> deleteFriend(Long userId, Long friendId);

    Optional<User> confirmFriendship(Long userId, Long friendId);
}
