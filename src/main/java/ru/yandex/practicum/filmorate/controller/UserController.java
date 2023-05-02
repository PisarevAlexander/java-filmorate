package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Integer userId) {
        if (userId < 1) {
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return userService.getById(userId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findUserFriends(@PathVariable("id") Integer userId) {
        if (userId < 1) {
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return userService.getFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("id") Integer userId, @PathVariable Integer otherId) {
        if (userId < 1 || otherId < 1) {
            throw new NotFoundException("Переменные id и otherId должны быль больше 0");
        }
        return userService.getCommonFriends(userId, otherId);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        throwIfNotValid(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        throwIfNotValid(user);
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        if (userId < 1 || friendId < 1) {
            throw new NotFoundException("Переменные id и friendId должны быль больше 0");
        }
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        if (userId < 1 || friendId < 1) {
            throw new NotFoundException("Переменные id и friendId должны быль больше 0");
        }
        userService.deleteFriend(userId, friendId);
    }

    public void throwIfNotValid(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new BadRequestException("email");
        }
        if (user.getLogin().isBlank()) {
            throw new BadRequestException("login");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new BadRequestException("birthday");
        }
    }
}
