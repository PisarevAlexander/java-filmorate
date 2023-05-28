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
}
