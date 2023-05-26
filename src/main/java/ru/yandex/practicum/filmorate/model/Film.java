package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
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

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
        genres = new TreeSet<>();
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
