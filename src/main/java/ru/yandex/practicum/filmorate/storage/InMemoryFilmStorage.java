package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 1;
    private Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilm() {
        log.info("Получен список всех фильсов");
        return new ArrayList<Film>(films.values());
    }

    @Override
    public Film getFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("id " + filmId + "не найден");
            throw new NotFoundException("id " + filmId + "не найден");
        }
        log.info("Запрос к фильму с id: {}", filmId);
        return films.get(filmId);
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(id);
        id++;
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return films.get(film.getId());
    }

    @Override
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка обновления: id {} не найден", film.getId());
            throw new NotFoundException("id " + film.getId() + "не найден");
        }
        films.put(film.getId(), film);
        log.info("Данные фильма обновлены: {}", film);
        return films.get(film.getId());
    }
}
