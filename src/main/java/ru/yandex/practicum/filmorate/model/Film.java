package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

import java.util.Set;
import java.util.TreeSet;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;

    private final Set<Integer> userLikeFilm = new TreeSet<>();

    public void addUserId(int friendId) {
        userLikeFilm.add(friendId);
    }

    public void deleteUserId(int friendId) {
        userLikeFilm.remove(friendId);
    }
}
