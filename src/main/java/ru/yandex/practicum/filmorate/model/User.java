package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private final Map<Integer, FriendStatus> friends = new HashMap();

    public void deleteFriendId(int friendId) {
        friends.remove(friendId);
    }
}
