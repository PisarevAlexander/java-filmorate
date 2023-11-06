package ru.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import ru.filmorate.model.enums.FilmGenre;
import ru.filmorate.model.enums.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The type Film
 */

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

    /**
     * Instantiates a new Film
     * @param id          the id
     * @param name        the name
     * @param description the description
     * @param releaseDate the release date
     * @param duration    the duration
     * @param mpa         the mpa
     */

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

    /**
     * Add user id
     * @param userId the user id
     */

    public void addUserId(int userId) {
        userLikeFilm.add(userId);
    }

    /**
     * Delete user id
     * @param friendId the friend id
     */

    public void deleteUserId(int friendId) {
        userLikeFilm.remove(friendId);
    }

    /**
     * Add genre
     * @param genre the genre
     */

    public void addGenre(FilmGenre genre) {
        genres.add(genre);
    }
}