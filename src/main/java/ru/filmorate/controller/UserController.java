package ru.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.filmorate.exception.BadRequestException;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.User;
import ru.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

/**
 * User controller
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /**
     * Find all list.
     * GET /users
     * @return the list of users
     */

    @GetMapping
    public List<User> findAll() {
        return userService.getAll();
    }

    /**
     * Find user by id
     * GET /users/{id}
     * @param userId the user id
     * @return the user
     */

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Integer userId) {
        if (userId < 1) {
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return userService.getById(userId);
    }

    /**
     * Find user friends
     * GET /users/{id}/friends
     * @param userId the user id
     * @return the list of users
     */

    @GetMapping("/{id}/friends")
    public List<User> findUserFriends(@PathVariable("id") Integer userId) {
        if (userId < 1) {
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return userService.getFriends(userId);
    }

    /**
     * Find common friends list
     * GET /users/{id}/friends/common/{otherId}
     * @param userId  the user id
     * @param otherId the other id
     * @return the list of users
     */

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("id") Integer userId, @PathVariable Integer otherId) {
        if (userId < 1 || otherId < 1) {
            throw new NotFoundException("Переменные id и otherId должны быль больше 0");
        }
        return userService.getCommonFriends(userId, otherId);
    }

    /**
     * Create user
     * POST /users
     * @param user the user
     * @return the user
     */

    @PostMapping
    public User create(@RequestBody User user) {
        throwIfNotValid(user);
        return userService.create(user);
    }

    /**
     * Update user
     * PUT /users
     * @param user the user
     * @return the user
     */

    @PutMapping
    public User update(@RequestBody User user) {
        throwIfNotValid(user);
        return userService.update(user);
    }

    /**
     * Add friend
     * PUT /users/{id}/friends/{friendId}
     * @param userId   the user id
     * @param friendId the friend id
     */

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        if (userId < 1 || friendId < 1) {
            throw new NotFoundException("Переменные id и friendId должны быль больше 0");
        }
        userService.addFriend(userId, friendId);
    }

    /**
     * Delete friend
     * DELETE /users/{id}/friends/{friendId}
     * @param userId   the user id
     * @param friendId the friend id
     */

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        if (userId < 1 || friendId < 1) {
            throw new NotFoundException("Переменные id и friendId должны быль больше 0");
        }
        userService.deleteFriend(userId, friendId);
    }

    /**
     * Throw if not valid
     * @param user the user
     */

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