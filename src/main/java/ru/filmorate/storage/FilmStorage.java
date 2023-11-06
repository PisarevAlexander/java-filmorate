package ru.filmorate.storage;

import ru.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

/**
 * The interface Film storage
 */

public interface FilmStorage {

    /**
     * Find all films
     * @return the list os films
     */

    List<Film> findAll();

    /**
     * Find film by id
     * @param filmId the film id
     * @return the optional of film
     */

    Optional<Film> findById(int filmId);

    /**
     * Find top films
     * @param count the count
     * @return the list of films
     */

    List<Film> findTop(int count);

    /**
     * Create film
     * @param film the film
     * @return the film
     */

    Film create(Film film);

    /**
     * Update film
     * @param film the film
     */

    void update(Film film);
}