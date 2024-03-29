package ru.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.User;
import ru.filmorate.model.enums.FriendStatus;
import ru.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type User service
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Qualifier("DBUsers")
    private final UserStorage userStorage;

    /**
     * Create user
     * @param user the user
     * @return the user
     */

    public User create(User user) {
        log.info("Добавлен новый пользователь: {}", user);
        return userStorage.create(user);
    }

    /**
     * Get all
     * @return the all users
     */

    public List<User> getAll() {
        log.info("Получен список всех пользователей");
        return userStorage.findAll();
    }

    /**
     * Update user
     * @param user the user
     * @return the user
     */

    public User update(User user) {
        userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("id " + user.getId() + " не найден"));
        userStorage.update(user);
        log.info("Данные пользователя обновлены: {}", user);
        return userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("id " + user.getId() + " не найден"));
    }

    /**
     * Get by id
     * @param id the id
     * @return the user
     */

    public User getById(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
    }

    /**
     * Add friend
     * @param userId   the user id
     * @param friendId the friend id
     */

    public void addFriend(int userId, int friendId) {
        User user1 = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("id " + userId + " не найден"));
        User user2 = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("id " + friendId + " не найден"));
        Map<Integer, FriendStatus> firstUserFriends = user1.getFriends();
        Map<Integer, FriendStatus> secondUserFriends = user2.getFriends();

        if (!firstUserFriends.containsKey(friendId) && !secondUserFriends.containsKey(userId)) {
            user1.addFriend(friendId, FriendStatus.NOT_CONFIRMED);
            userStorage.update(user1);
            log.info("Пользователи: id {} направил заявку в друзья пользователю с id {}", user1.getId(), user2.getId());
        }

        if (!firstUserFriends.containsKey(friendId) && secondUserFriends.containsKey(userId)) {
            user1.addFriend(friendId, FriendStatus.CONFIRMED);
            user2.addFriend(userId, FriendStatus.CONFIRMED);
            userStorage.update(user1);
            userStorage.update(user2);
            log.info("Пользователи: id {} и id {} теперь друзья", user1.getId(), user2.getId());
        }
    }

    /**
     * Delete friend
     * @param userId   the user id
     * @param friendId the friend id
     */

    public void deleteFriend(int userId, int friendId) {
        User user1 = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("id " + userId + " не найден"));
        User user2 = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("id " + friendId + " не найден"));
        user1.deleteFriendId(friendId);
        user2.deleteFriendId(userId);
        userStorage.update(user1);
        userStorage.update(user2);
        log.info("Пользователи: id {} и id {} больше не друзья", user1.getId(), user2.getId());
    }

    /**
     * Get friends
     * @param userId the user id
     * @return the friends
     */

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("id " + userId + " не найден"));
        Map<Integer, FriendStatus> userFriends = user.getFriends();
        for (Integer id : userFriends.keySet()) {
                friends.add(userStorage.findById(id).get());
        }
        return friends;
    }

    /**
     * Get common friends
     * @param userId  the user id
     * @param otherId the other id
     * @return the common friends
     */

    public List<User> getCommonFriends(int userId, int otherId) {
        List<User> friends = new ArrayList<>();
        User user1 = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("id " + userId + " не найден"));
        User user2 = userStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("id " + otherId + " не найден"));
        Map<Integer, FriendStatus> userFriends = user1.getFriends();
        Map<Integer, FriendStatus> otherFriends = user2.getFriends();

        for (Integer userFriend : userFriends.keySet()) {
            for (Integer otherFriend : otherFriends.keySet()) {
                if (userFriend == otherFriend) {
                    friends.add(userStorage.findById(userFriend).get());
                }
            }
        }
        return friends;
    }
}