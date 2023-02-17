package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int autoGeneratingId = 0;


    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getById(Integer id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(++autoGeneratingId);

        checkIfUserNameIsEmpty(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Integer id) {
        return users.remove(id);
    }

    @Override
    public User update(User user) {
        checkIfUserNameIsEmpty(user);
        users.put(user.getId(), user);
        return user;
    }

    private void checkIfUserNameIsEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
