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
import java.util.Optional;

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

    public List<Film> getAllFilm() {
        log.info("Получен список всех фильмов");
        return filmStorage.findAllFilm();
    }

    public Film update(Film film) {
        Optional<Film> updatedFilm = filmStorage.update(film);
        if (updatedFilm.isEmpty()) {
            log.warn("Ошибка обновления: id {} не найден", film.getId());
            throw new NotFoundException("id " + film.getId() + "не найден");
        }
        log.info("Данные фильма обновлены: {}", film);
        return updatedFilm.get();
    }

    public Film getFilmById(int id) {
        Optional<Film> film = filmStorage.findFilmById(id);
        if (film.isEmpty()) {
            log.warn("id " + id + "не найден");
            throw new NotFoundException("id " + id + "не найден");
        }
        log.info("Запрос к фильму с id: {}", id);
        return film.get();
    }

    public void addLike(int id, int userId) {
        Optional<Film> film = filmStorage.findFilmById(id);
        Optional<User> user = userStorage.findUserById(userId);
        if (film.isEmpty()) {
            log.warn("id " + id + "не найден");
            throw new NotFoundException("id " + id + "не найден");
        } else if (user.isEmpty()) {
            log.warn("id " + userId + "не найден");
            throw new NotFoundException("id " + userId + "не найден");
        } else {
            Film likedFilm = film.get();
            likedFilm.addUserId(user.get().getId());
            filmStorage.update(likedFilm);
            log.info("Пользователю id: {} понравился фильм с id: {}", userId, id);
        }
    }

    public void deleteLike(int id, int userId) {
        Optional<Film> film = filmStorage.findFilmById(id);
        Optional<User> user = userStorage.findUserById(userId);
        if (film.isEmpty()) {
            log.warn("id " + id + "не найден");
            throw new NotFoundException("id " + id + "не найден");
        } else if (user.isEmpty()) {
            log.warn("id " + userId + "не найден");
            throw new NotFoundException("id " + userId + "не найден");
        } else {
            Film unlikedFilm = film.get();
            unlikedFilm.deleteUserId(user.get().getId());
            filmStorage.update(unlikedFilm);
            log.info("Пользователю id: {} больше не нравиться фильм с id: {}", userId, id);
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.findTopFilms(count);
    }
}