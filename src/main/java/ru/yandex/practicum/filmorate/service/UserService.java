package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(int userId, int friendId) {
            User user1 = userStorage.getUserById(userId);
            User user2 = userStorage.getUserById(friendId);
            user1.addFriendId(friendId);
            user2.addFriendId(userId);
            userStorage.updateUser(user1);
            userStorage.updateUser(user2);
            log.info("Пользователи: id {} и id {} теперь друзья", user1.getId(), user2.getId());
    }

    public void deleteFriend(int userId, int friendId) {
        User user1 = userStorage.getUserById(userId);
        User user2 = userStorage.getUserById(friendId);
        user1.deleteFriendId(friendId);
        user2.deleteFriendId(userId);
        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
        log.info("Пользователи: id {} и id {} больше не друзья", user1.getId(), user2.getId());
    }

    public List<User> getFriends(int userId) {
        List<User> friends = new ArrayList<>();
        Set<Integer> friendsId = userStorage.getUserById(userId).getFriendsId();
        for (Integer id : friendsId) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        List<User> friends = new ArrayList<>();
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriendsId();
        Set<Integer> otherFriends = userStorage.getUserById(otherId).getFriendsId();
        for (Integer userFriend : userFriends) {
            for (Integer otherFriend : otherFriends) {
                if (userFriend == otherFriend) {
                    friends.add(userStorage.getUserById(userFriend));
                }
            }
        }
        return friends;
    }
}
