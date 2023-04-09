package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AbstractStorage<T> {
    private final Map<Long, T> data = new HashMap<>();
    private Long autoGeneratingId = 0L;

    protected Long getNextId() {
        return ++autoGeneratingId;
    }

    protected boolean notContainsId(Long id) {
        return !data.containsKey(id);
    }

    protected void validate(Long id) {
        // По-умолчанию валидация не требуется
    }

    protected void fix(T t) {
        // По-умолчанию исправления не требуются
    }

    public Collection<T> getAll() {
        return data.values();
    }

    public Optional<T> getById(Long id) {
        validate(id);
        return Optional.of(data.get(id));
    }

    public void create(Long id, T t) {
        fix(t);
        data.put(id, t);
    }

    public Optional<T> delete(Long id) {
        return Optional.of(data.remove(id));
    }

    public void update(Long id, T t) {
        validate(id);
        fix(t);
        data.put(id, t);
    }
}
