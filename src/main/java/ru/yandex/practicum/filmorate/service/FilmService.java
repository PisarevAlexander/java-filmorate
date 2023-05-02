package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        log.info("Добавлен новый фильм: {}", film);
        return filmStorage.create(film);
    }

    public List<Film> getAll() {
        log.info("Получен список всех фильмов");
        return filmStorage.findAll();
    }

    public Film update(Film film) {
        filmStorage.findById(film.getId())
                .orElseThrow(() -> new NotFoundException("id " + film.getId() + " не найден"));
        log.info("Данные фильма обновлены: {}", film);
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
    }

    public void addLike(int id, int userId) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("id " + userId + " не найден"));
        film.addUserId(user.getId());
        filmStorage.update(film);
        log.info("Пользователю id: {} понравился фильм с id: {}", userId, id);
    }

    public void deleteLike(int id, int userId) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("id " + userId + " не найден"));
        film.deleteUserId(user.getId());
        filmStorage.update(film);
        log.info("Пользователю id: {} больше не нравиться фильм с id: {}", userId, id);
    }

    public List<Film> getTop(int count) {
        return filmStorage.findTop(count);
    }
}