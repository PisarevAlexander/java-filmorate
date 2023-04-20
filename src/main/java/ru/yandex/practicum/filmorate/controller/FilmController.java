package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private int id = 1;
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<Film>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        throwIfNotValid(film);
        film.setId(id);
        id++;
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        throwIfNotValid(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка обновления: id {} не найден", film.getId());
            throw new NotFoundException("id " + film.getId() + "не найден");
        }
        films.put(film.getId(), film);
        log.info("Данные фильма обновлены: {}", film);
        return films.get(film.getId());
    }

    public void throwIfNotValid(Film film) {
        if (film.getName().isBlank()) {
            log.warn("Ошибка валидации: name не может быть пустым");
            throw new BadRequestException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: длина description {} знаков", film.getDescription().length());
            throw new BadRequestException("Описание не должно быть длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации: releaseDate {} что раньше 1895-12-28", film.getReleaseDate());
            throw new BadRequestException("Дата релиза не может быть раньше 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка валидации: duration {} меньше 0", film.getDuration());
            throw new BadRequestException("Продолжительность фильма должна быть больше 0");
        }
    }
}