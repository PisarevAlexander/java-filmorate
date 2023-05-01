package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAllFilm();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Integer filmId) {
        if (filmId < 1) {
            log.warn("id = {} , а должен быть больше 0", filmId);
            throw new NotFoundException("Переменная id должна быль больше 0");
        }
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> topFilms(@RequestParam(defaultValue = "10") String count) {
        if (Integer.parseInt(count) < 1) {
            log.warn("count = {} , а должен быть больше 0", count);
            throw new BadRequestException("count");
        }
        return filmService.getTopFilms(Integer.parseInt(count));
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
            log.warn("id = {}, userId = {} , а должны быть больше 0", filmId, userId);
            throw new NotFoundException("Переменные id и userId должны быль больше 0");
        }
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        if (filmId < 1 || userId < 1) {
            log.warn("id = {}, userId = {} , а должны быть больше 0", filmId, userId);
            throw new NotFoundException("Переменные id и userId должны быль больше 0");
        }
        filmService.deleteLike(filmId, userId);
    }

    public void throwIfNotValid(Film film) {
        if (film.getName().isBlank()) {
            log.warn("Ошибка валидации: name не может быть пустым");
            throw new BadRequestException("name");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: длина description {} знаков", film.getDescription().length());
            throw new BadRequestException("description");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации: releaseDate {} что раньше 1895-12-28", film.getReleaseDate());
            throw new BadRequestException("releaseDate");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка валидации: duration {} меньше 0", film.getDuration());
            throw new BadRequestException("duration");
        }
    }
}