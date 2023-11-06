package ru.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.filmorate.exception.BadRequestException;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.Film;
import ru.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

/**
 * Film controller
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    /**
     * Find all list
     * GET /films
     * @return the list of films
     */

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAll();
    }

    /**
     * Find film by id film
     * GET /films/{id}
     * @param filmId the film id
     * @return the film
     */

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Integer filmId) {
        if (filmId < 1) {
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return filmService.getById(filmId);
    }

    /**
     * Top films list
     * GET /films/popular
     * @param count the count
     * @return the list of films
     */

    @GetMapping("/popular")
    public List<Film> topFilms(@RequestParam(defaultValue = "10") String count) {
        if (Integer.parseInt(count) < 1) {
            throw new BadRequestException("count");
        }
        return filmService.getTop(Integer.parseInt(count));
    }

    /**
     * Create film
     * POST /films
     * @param film the film
     * @return the film
     */

    @PostMapping
    public Film create(@RequestBody Film film) {
        throwIfNotValid(film);
        return filmService.create(film);
    }

    /**
     * Update film
     * PUT /films
     * @param film the film
     * @return the film
     */

    @PutMapping
    public Film update(@RequestBody Film film) {
        throwIfNotValid(film);
        return filmService.update(film);
    }

    /**
     * Add like
     * PUT /films/{id}/like/{userId}
     * @param filmId the film id
     * @param userId the user id
     */

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        if (filmId < 1 || userId < 1) {
            throw new NotFoundException("Переменные id и userId должны быль больше 0");
        }
        filmService.addLike(filmId, userId);
    }

    /**
     * Delete like
     * DELETE /films/{id}/like/{userId}
     * @param filmId the film id
     * @param userId the user id
     */

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        if (filmId < 1 || userId < 1) {
            throw new NotFoundException("Переменные id и userId должны быль больше 0");
        }
        filmService.deleteLike(filmId, userId);
    }

    /**
     * Throw if not valid
     * @param film the film
     */

    public void throwIfNotValid(Film film) {
        if (film.getName().isBlank()) {
            throw new BadRequestException("name");
        }
        if (film.getDescription().length() > 200) {
            throw new BadRequestException("description");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new BadRequestException("releaseDate");
        }
        if (film.getDuration() <= 0) {
            throw new BadRequestException("duration");
        }
    }
}