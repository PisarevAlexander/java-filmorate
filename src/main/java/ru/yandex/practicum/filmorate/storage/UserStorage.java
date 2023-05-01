package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAllUsers();

    Optional<User> findUserById(int userId);

    User create(User user);

    Optional<User> update(User user);
}
