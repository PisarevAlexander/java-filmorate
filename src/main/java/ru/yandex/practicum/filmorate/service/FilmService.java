package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("DBFilms")
    private final FilmStorage filmStorage;

    @Qualifier("DBUsers")
    private final UserStorage userStorage;
    private final GenreDbStorage genreDbStorage;

    public Film create(Film film) {
        log.info("Добавлен новый фильм: {}", film);
        return filmStorage.create(film);
    }

    public List<Film> getAll() {
        List<Film> films = filmStorage.findAll();
        Map<Integer, Film> mapFilms = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        List<Map<String, Object>> genres = genreDbStorage.findGenreByFilms();
        List<Map<String, Object>> likedUsers = userStorage.findUsersLikedFilms();
        for (Map<String, Object> genre : genres) {
            mapFilms.get(Integer.parseInt(genre.get("FILM_ID").toString())).getGenres()
                    .add(FilmGenre.valueOf(genre.get("GENRE").toString()));
        }
        for (Map<String, Object> likedUser : likedUsers) {
            mapFilms.get(Integer.parseInt(likedUser.get("FILM_ID").toString())).getUserLikeFilm()
                    .add(Integer.parseInt(likedUser.get("USER_ID").toString()));
        }
        log.info("Получен список всех фильмов");
        return films;
    }

    public Film update(Film film) {
        filmStorage.findById(film.getId())
                .orElseThrow(() -> new NotFoundException("id " + film.getId() + " не найден"));
        log.info("Данные фильма обновлены: {}", film);
        filmStorage.update(film);
        return filmStorage.findById(film.getId())
                .orElseThrow(() -> new NotFoundException("id " + film.getId() + " не найден"));
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