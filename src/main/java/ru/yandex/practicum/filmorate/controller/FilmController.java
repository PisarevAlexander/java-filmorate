package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private int id = 1;
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
       if (validator(film) && !films.containsKey(film.getId())) {
           films.put(film.getId(), film);
           log.info("Добавлен новый фильм: {}", film);
       }
       return films.get(film.getId());
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (validator(film) && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Данные фильма обновлены: {}", film);
        } else {
            log.warn("Ошибка обновления: id {} не найден", film.getId());
            throw new NotFoundException("id " + film.getId() + "не найден");
        }
        return films.get(film.getId());
    }

    public boolean validator(Film film) {
        if (film.getName().isBlank()) {
            log.warn("Ошибка валидации: name не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: длина description {} знаков", film.getDescription().length());
            throw new ValidationException("Описание не должно быть длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации: releaseDate {} что раньше 1895-12-28", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка валидации: duration {} меньше 0", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
        if (film.getId() == 0) {
            film.setId(id);
            id++;
        } else if (id == film.getId()) {
            id++;
        }
        return true;
    }
}