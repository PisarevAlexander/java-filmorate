package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id);
        User user = userStorage.getUserById(userId);
        film.addUserId(user.getId());
        filmStorage.updateFilm(film);
        log.info("Пользователю id: {} понравился фильм с id: {}", userId, id);
    }

    public void deleteLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id);
        User user = userStorage.getUserById(userId);
        film.deleteUserId(user.getId());
        filmStorage.updateFilm(film);
        log.info("Пользователю id: {} больше не нравиться фильм с id: {}", userId, id);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> topFilm = filmStorage.getAllFilm().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getUserLikeFilm().size(), f1.getUserLikeFilm().size()))
                .limit(count)
                .collect(Collectors.toList());
        return topFilm;
    }
}