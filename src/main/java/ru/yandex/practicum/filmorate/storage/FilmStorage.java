package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAllFilm();

    Optional<Film> findFilmById(int filmId);

    List<Film> findTopFilms(int count);

    Film create(Film film);

    Optional<Film> update(Film film);
}
