package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FilmGenre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    private int id;
    private String name;

    @JsonCreator
    public static FilmGenre forValues(@JsonProperty("id") int id) {
        for (FilmGenre genre : FilmGenre.values()) {
            if (genre.id == id) {
                return genre;
            }
        }
        return null;
    }
}
