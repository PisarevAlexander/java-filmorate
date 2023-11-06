package ru.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum Mpa
 */

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Mpa {
    G(1, "G"),
    PG(2, "PG"),
    PG13(3, "PG-13"),
    R(4, "R"),
    NC17(5, "NC-17");

    private int id;
    private String name;

    /**
     * For values mpa.
     * @param id the id
     * @return the mpa
     */

    @JsonCreator
    public static Mpa forValues(@JsonProperty("id") int id) {
        for (Mpa mpa : Mpa.values()) {
            if (mpa.id == id) {
                return mpa;
            }
        }
        return null;
    }
}