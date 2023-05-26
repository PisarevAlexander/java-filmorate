package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FriendStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Integer, FriendStatus> friends;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        friends =  new HashMap();
    }

    public void deleteFriendId(int friendId) {
        friends.remove(friendId);
    }

    public void addFriend(int friendId, FriendStatus friendStatus) {
        friends.put(friendId, friendStatus);
    }
}
