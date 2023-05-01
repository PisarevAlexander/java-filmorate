package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 1;
    private Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findAllFilm() {
        return new ArrayList<Film>(films.values());
    }

    @Override
    public Optional<Film> findFilmById(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public List<Film> findTopFilms(int count) {
        List<Film> topFilm = new ArrayList<Film>(films.values()).stream()
                .sorted((f1, f2) -> Integer.compare(f2.getUserLikeFilm().size(), f1.getUserLikeFilm().size()))
                .limit(count)
                .collect(Collectors.toList());
        return topFilm;
    }

    @Override
    public Film create(Film film) {
        film.setId(id);
        id++;
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Optional<Film> update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        return Optional.ofNullable(films.get(film.getId()));
    }
}
