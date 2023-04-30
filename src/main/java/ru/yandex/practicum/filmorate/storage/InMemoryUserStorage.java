package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        log.info("Получен список всех пользователей");
        return new ArrayList<User>(users.values());
    }

    @Override
    public User getUserById(int userId) {
        if (!users.containsKey(userId)) {
            log.warn("id " + userId + "не найден");
            throw new NotFoundException("id " + userId + "не найден");
        }
        log.info("Запрос к пользователю с id: {}", userId);
        return users.get(userId);
    }

    @Override
    public User addUser(User user) {
        user.setId(id);
        id++;
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка обновления: id {} не найден", user.getId());
            throw new NotFoundException("id " + user.getId() + "не найден");
        }
        users.put(user.getId(), user);
        log.info("Данные пользователя обновлены: {}", user);
        return users.get(user.getId());
    }
}
