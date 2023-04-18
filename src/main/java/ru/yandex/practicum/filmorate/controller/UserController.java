package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private int id = 1;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (validator(user) && !users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь: {}", user);
        }
        return users.get(user.getId());
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (validator(user) && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Данные пользователя обновлены: {}", user);
        } else {
            log.warn("Ошибка обновления: id {} не найден", user.getId());
            throw new NotFoundException("id " + user.getId() + "не найден");
        }
        return users.get(user.getId());
    }

    public boolean validator(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email {} не корректный", user.getEmail());
            throw new ValidationException("Не корректный email");
        }
        if (user.getLogin().isBlank()) {
            log.warn("Ошибка валидации: login {} не корректный", user.getLogin());
            throw new ValidationException("Не корректный логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: birthday {} не корректный", user.getBirthday());
            throw new ValidationException("Не корректная дата рождения");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(id);
            id++;
        } else if (id == user.getId()) {
            id++;
        }
        return true;
    }
}
