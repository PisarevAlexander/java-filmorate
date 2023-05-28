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
import java.util.Set;
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
        Film newFilm = filmStorage.create(film);
        if (!film.getGenres().isEmpty()) {
            Set<FilmGenre> genres = film.getGenres();
            for (FilmGenre genre : genres) {
                genreDbStorage.addGenre(newFilm.getId(), genre.getId());
            }
        }
        log.info("Добавлен новый фильм: {}", film);
        return newFilm;
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
        getById(film.getId());
        filmStorage.update(film);
        genreDbStorage.deleteGenresForFilm(film.getId());
        if (!film.getGenres().isEmpty()) {
            Set<FilmGenre> genres = film.getGenres();
            for (FilmGenre genre : genres) {
                genreDbStorage.addGenre(film.getId(), genre.getId());
            }
        }
        if (!film.getUserLikeFilm().isEmpty()) {
            userStorage.deleteUsersLikedFilms(film.getId());
            Set<Integer> likedUsers = film.getUserLikeFilm();
            for (Integer likedUser : likedUsers) {
                userStorage.addUsersLikedFilms(film.getId(), likedUser);
            }
        }
        log.info("Данные фильма обновлены: {}", film);
        return getById(film.getId());
    }

    public Film getById(int id) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
        List<Map<String, Object>> genres = genreDbStorage.findGenreByFilmsId(id);
        List<Map<String, Object>> likedUsers = userStorage.findUsersLikedFilmsById(id);
        for (Map<String, Object> genre : genres) {
            film.addGenre(FilmGenre.valueOf(genre.get("GENRE").toString()));
        }
        for (Map<String, Object> likedUser : likedUsers) {
            film.getUserLikeFilm().add(Integer.parseInt(likedUser.get("USER_ID").toString()));
        }
        return film;
    }

    public void addLike(int id, int userId) {
        Film film = getById(id);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("id " + userId + " не найден"));
        film.addUserId(user.getId());
        update(film);
        log.info("Пользователю id: {} понравился фильм с id: {}", userId, id);
    }

    public void deleteLike(int id, int userId) {
        Film film = getById(id);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("id " + userId + " не найден"));
        film.deleteUserId(user.getId());
        update(film);
        log.info("Пользователю id: {} больше не нравиться фильм с id: {}", userId, id);
    }

    public List<Film> getTop(int count) {
        List<Film> films = filmStorage.findTop(count);
        Map<Integer, Film> mapFilms = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        List<Map<String, Object>> genres = genreDbStorage.findGenreForTopFilms(count);
        List<Map<String, Object>> likedUsers = userStorage.findUsersLikedFilmsForTopFilms(count);
        for (Map<String, Object> genre : genres) {
            mapFilms.get(Integer.parseInt(genre.get("FILM_ID").toString())).getGenres()
                    .add(FilmGenre.valueOf(genre.get("GENRE").toString()));
        }
        for (Map<String, Object> likedUser : likedUsers) {
            mapFilms.get(Integer.parseInt(likedUser.get("FILM_ID").toString())).getUserLikeFilm()
                    .add(Integer.parseInt(likedUser.get("USER_ID").toString()));
        }
        log.info("Получен топ фильмов");
        return filmStorage.findTop(count);
    }
}