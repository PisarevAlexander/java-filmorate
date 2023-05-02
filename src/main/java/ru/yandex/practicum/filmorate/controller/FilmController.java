package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Integer filmId) {
        if (filmId < 1) {
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return filmService.getById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> topFilms(@RequestParam(defaultValue = "10") String count) {
        if (Integer.parseInt(count) < 1) {
            throw new BadRequestException("count");
        }
        return filmService.getTop(Integer.parseInt(count));
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        throwIfNotValid(film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        throwIfNotValid(film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        if (filmId < 1 || userId < 1) {
            throw new NotFoundException("Переменные id и userId должны быль больше 0");
        }
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        if (filmId < 1 || userId < 1) {
            throw new NotFoundException("Переменные id и userId должны быль больше 0");
        }
        filmService.deleteLike(filmId, userId);
    }

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