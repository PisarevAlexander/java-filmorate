package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private int id = 1;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<User>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        throwIfNotValid(user);
        user.setId(id);
        id++;
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return users.get(user.getId());
    }

    @PutMapping
    public User update(@RequestBody User user) {
        throwIfNotValid(user);
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка обновления: id {} не найден", user.getId());
            throw new NotFoundException("id " + user.getId() + "не найден");
        }
        users.put(user.getId(), user);
        log.info("Данные пользователя обновлены: {}", user);
        return users.get(user.getId());
    }

    public void throwIfNotValid(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email {} не корректный", user.getEmail());
            throw new BadRequestException("Не корректный email");
        }
        if (user.getLogin().isBlank()) {
            log.warn("Ошибка валидации: login {} не корректный", user.getLogin());
            throw new BadRequestException("Не корректный логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: birthday {} не корректный", user.getBirthday());
            throw new BadRequestException("Не корректная дата рождения");
        }
    }
}
