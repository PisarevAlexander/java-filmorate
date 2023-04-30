package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
@AllArgsConstructor
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private final Set<Integer> friendsId = new TreeSet<>();

    public void addFriendId(int friendId) {
        friendsId.add(friendId);
    }

    public void deleteFriendId(int friendId) {
        friendsId.remove(friendId);
    }
}
