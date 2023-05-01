package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        log.info("Добавлен новый пользователь: {}", user);
        return userStorage.create(user);
    }

    public List<User> getAllUser() {
        log.info("Получен список всех пользователей");
        return userStorage.findAllUsers();
    }

    public User update(User user) {
        Optional<User> updatedUser = userStorage.update(user);
        if (updatedUser.isEmpty()) {
            log.warn("Ошибка обновления: id {} не найден", user.getId());
            throw new NotFoundException("id " + user.getId() + "не найден");
        }
        log.info("Данные пользователя обновлены: {}", user);
        return updatedUser.get();
    }

    public User getUserById(int id) {
        Optional<User> user = userStorage.findUserById(id);
        if (user.isEmpty()) {
            log.warn("id " + id + "не найден");
            throw new NotFoundException("id " + id + "не найден");
        }
        log.info("Запрос к пользователю с id: {}", id);
        return user.get();
    }

    public void addFriend(int userId, int friendId) {
        Optional<User> user1 = userStorage.findUserById(userId);
        Optional<User> user2 = userStorage.findUserById(friendId);
        if (user1.isEmpty()) {
            log.warn("id " + userId + "не найден");
            throw new NotFoundException("id " + userId + "не найден");
        } else if (user2.isEmpty()) {
            log.warn("id " + friendId + "не найден");
            throw new NotFoundException("id " + friendId + "не найден");
        } else {
        User user = user1.get();
        User friend = user2.get();
        user.addFriendId(friendId);
        friend.addFriendId(userId);
        userStorage.update(user);
        userStorage.update(friend);
        log.info("Пользователи: id {} и id {} теперь друзья", user.getId(), friend.getId());
        }
    }

    public void deleteFriend(int userId, int friendId) {
        Optional<User> user1 = userStorage.findUserById(userId);
        Optional<User> user2 = userStorage.findUserById(friendId);
        if (user1.isEmpty()) {
            log.warn("id " + userId + "не найден");
            throw new NotFoundException("id " + userId + "не найден");
        } else if (user2.isEmpty()) {
            log.warn("id " + friendId + "не найден");
            throw new NotFoundException("id " + friendId + "не найден");
        } else {
            User user = user1.get();
            User friend = user2.get();
            user.deleteFriendId(friendId);
            friend.deleteFriendId(userId);
            userStorage.update(user);
            userStorage.update(friend);
            log.info("Пользователи: id {} и id {} больше не друзья", user.getId(), friend.getId());
        }
    }

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        Optional<User> user = userStorage.findUserById(userId);
        if (user.isEmpty()) {
            log.warn("id " + userId + "не найден");
            throw new NotFoundException("id " + userId + "не найден");
        }
        Set<Integer> friendsId = user.get().getFriendsId();
        for (Integer id : friendsId) {
            friends.add(userStorage.findUserById(id).get());
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        List<User> friends = new ArrayList<>();
        Optional<User> user1 = userStorage.findUserById(userId);
        Optional<User> user2 = userStorage.findUserById(otherId);
        if (user1.isEmpty()) {
            log.warn("id " + userId + "не найден");
            throw new NotFoundException("id " + userId + "не найден");
        } else if (user2.isEmpty()) {
            log.warn("id " + otherId + "не найден");
            throw new NotFoundException("id " + otherId + "не найден");
        } else {
            Set<Integer> userFriends = user1.get().getFriendsId();
            Set<Integer> otherFriends = user2.get().getFriendsId();
            for (Integer userFriend : userFriends) {
                for (Integer otherFriend : otherFriends) {
                    if (userFriend == otherFriend) {
                        friends.add(userStorage.findUserById(userFriend).get());
                    }
                }
            }
        }
        return friends;
    }
}
