package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FilmGenre;
import ru.yandex.practicum.filmorate.model.enums.Mpa;

import java.time.LocalDate;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Mpa mpa;
    private Set<Integer> userLikeFilm;
    private Set<FilmGenre> genres;

    public Film(int id, String name, String description, LocalDate releaseDate, Long duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        userLikeFilm = new LinkedHashSet<>();
        genres = new LinkedHashSet<>();
    }

    public void addUserId(int userId) {
        userLikeFilm.add(userId);
    }

    public void deleteUserId(int friendId) {
        userLikeFilm.remove(friendId);
    }

    public void addGenre(FilmGenre genre) {
        genres.add(genre);
    }
}
