package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component("InMemoryUsers")
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<User>(users.values());
    }

    @Override
    public Optional<User> findById(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User create(User user) {
        user.setId(id);
        id++;
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public List<Map<String, Object>> findUsersLikedFilms() {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> findUsersLikedFilmsById(int id) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> findUsersLikedFilmsForTopFilms(int count) {
        return List.of();
    }

    @Override
    public void deleteUsersLikedFilms(int filmId) {
    }

    @Override
    public void addUsersLikedFilms(int filmId, int userId) {
    }
}
