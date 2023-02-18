package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User addUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        return userStorage.getById(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        Set<Long> userFriends = user.getFriends();
        userFriends.add(friendId);

        Set<Long> friendFriends = friend.getFriends();
        friendFriends.add(userId);

        userStorage.update(friend.toBuilder().friends(friendFriends).build());
        return userStorage.update(user.toBuilder().friends(userFriends).build());
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        Set<Long> userFriends = user.getFriends();
        userFriends.remove(friendId);

        Set<Long> friendFriends = friend.getFriends();
        friendFriends.remove(userId);

        userStorage.update(friend.toBuilder().friends(friendFriends).build());
        return userStorage.update(user.toBuilder().friends(userFriends).build());
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        Set<Long> userFriends = new HashSet<>(user.getFriends());
        Set<Long> friendFriends = new HashSet<>(friend.getFriends());

        userFriends.retainAll(friendFriends);

        List<User> result = new ArrayList<>();

        for (Long id : userFriends) {
            result.add(userStorage.getById(id));
        }

        return result;
    }

    public List<User> getAllFriends(Long userId) {
        Set<Long> friendIds = userStorage.getById(userId).getFriends();
        List<User> result = new ArrayList<>();

        for (Long friendId : friendIds) {
            result.add(userStorage.getById(friendId));
        }

        return result;
    }
}
