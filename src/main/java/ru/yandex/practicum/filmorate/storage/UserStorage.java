package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    Optional<User> findById(int userId);

    User create(User user);

    void update(User user);

    List<Map<String, Object>> findUsersLikedFilms();

    List<Map<String, Object>> findUsersLikedFilmsById(int id);

    List<Map<String, Object>> findUsersLikedFilmsForTopFilms(int count);

    void deleteUsersLikedFilms(int filmId);

    void addUsersLikedFilms(int filmId, int userId);
}
