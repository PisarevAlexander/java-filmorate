package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Integer userId) {
        if (userId < 1) {
            log.warn("id = {} , а должен быть больше 0", userId);
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return userStorage.getUserById(userId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findUserFriends(@PathVariable("id") Integer userId) {
        if (userId < 1) {
            log.warn("id = {} , а должен быть больше 0", userId);
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return userService.getFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("id") Integer userId, @PathVariable Integer otherId) {
        if (userId < 1 || otherId < 1) {
            log.warn("id = {}, otherId = {} , а должны быть больше 0", userId, otherId);
            throw new NotFoundException("Переменные id и otherId должны быль больше 0");
        }
        return userService.getCommonFriends(userId, otherId);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        throwIfNotValid(user);
        return userStorage.addUser(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        throwIfNotValid(user);
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        if (userId < 1 || friendId < 1) {
            log.warn("id = {}, friendId = {} , а должны быть больше 0", userId, friendId);
            throw new NotFoundException("Переменные id и friendId должны быль больше 0");
        }
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        if (userId < 1 || friendId < 1) {
            log.warn("id = {}, friendId = {} , а должны быть больше 0", userId, friendId);
            throw new NotFoundException("Переменные id и friendId должны быль больше 0");
        }
        userService.deleteFriend(userId, friendId);
    }

    public void throwIfNotValid(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email {} не корректный", user.getEmail());
            throw new IncorrectParameterException("email");
        }
        if (user.getLogin().isBlank()) {
            log.warn("Ошибка валидации: login {} не корректный", user.getLogin());
            throw new IncorrectParameterException("login");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: birthday {} не корректный", user.getBirthday());
            throw new IncorrectParameterException("birthday");
        }
    }
}
