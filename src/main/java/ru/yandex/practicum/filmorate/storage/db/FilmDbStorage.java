package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.FilmGenre;
import ru.yandex.practicum.filmorate.model.enums.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Repository("DBFilms")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("select f.film_id, f.name, f.description, f.release_date, " +
                        "f.duration, r.rating from films as f left join mpa_rating as r on f.rating_id = r.rating_id",
                (rs, rowNum) -> new Film(
                        rs.getInt("film_id"), rs.getString("name"),
                        rs.getString("description"), rs.getDate("release_date").toLocalDate(),
                        rs.getLong("duration"), Mpa.valueOf(rs.getString("rating"))));
        for (Film film : films) {
            SqlRowSet genres = jdbcTemplate.queryForRowSet("select g.genre from films_genre as f join genres as g on " +
                            "f.genre_id = g.genre_id where film_id = ?",
                    film.getId());
            while (genres.next()) {
                film.addGenre(FilmGenre.valueOf(genres.getString("genre")));
            }
        }
        return findLikedUsers(films);
    }

    @Override
    public Optional<Film> findById(int filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select f.film_id, f.name, f.description, f.release_date, " +
                        "f.duration, r.rating from films as f left join mpa_rating as r on f.rating_id = r.rating_id " +
                        "where film_id = ?",
                filmId);
        if (filmRows.next()) {
            Film film = new Film(filmRows.getInt("film_id"), filmRows.getString("name"),
                    filmRows.getString("description"), filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getLong("duration"), Mpa.valueOf(filmRows.getString("rating")));
            SqlRowSet likedUsers = jdbcTemplate.queryForRowSet("select user_id from users_liked_films " +
                    "where film_id = ?", film.getId());
            while (likedUsers.next()) {
                film.addUserId(likedUsers.getInt("user_id"));
            }
            SqlRowSet genres = jdbcTemplate.queryForRowSet("select g.genre from films_genre as f join genres as g on " +
                            "f.genre_id = g.genre_id where film_id = ?",
                    film.getId());
            while (genres.next()) {
                film.addGenre(FilmGenre.valueOf(genres.getString("genre")));
            }
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findTop(int count) {
        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, r.rating " +
                "from films as f left join mpa_rating as r on f.rating_id = r.rating_id left join users_liked_films " +
                "as l on f.film_id = l.film_id group by f.film_id order by sum(l.user_id) desc limit ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Film(
                rs.getInt("film_id"), rs.getString("name"),
                rs.getString("description"), rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"), Mpa.valueOf(rs.getString("rating"))), count);
        return findLikedUsers(films);
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "insert into films(name, description, release_date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setObject(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        if (!film.getGenres().isEmpty()) {
            setGenres(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update films set name = ?, description = ?, release_date = ?, duration = ?, " +
                "rating_id = ? where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (!film.getUserLikeFilm().isEmpty()) {
            jdbcTemplate.update("delete users_liked_films where film_id = ?", film.getId());
            Set<Integer> likedUsers = film.getUserLikeFilm();
            for (Integer likedUser : likedUsers) {
                jdbcTemplate.update("insert into users_liked_films(film_id, user_id) values (?, ?)",
                        film.getId(), likedUser);
            }
        }
        jdbcTemplate.update("delete films_genre where film_id = ?", film.getId());
        if (!film.getGenres().isEmpty()) {
            setGenres(film);
        }
        return film;
    }

    private List<Film> findLikedUsers(List<Film> films) {
        for (Film film : films) {
            SqlRowSet likedUsers = jdbcTemplate.queryForRowSet("select user_id from users_liked_films " +
                    "where film_id = ?", film.getId());
            while (likedUsers.next()) {
                film.addUserId(likedUsers.getInt("user_id"));
            }
        }
        return films;
    }

    private void setGenres(Film film) {
        Set<FilmGenre> genres = film.getGenres();
        for (FilmGenre genre : genres) {
            jdbcTemplate.update("insert into films_genre(film_id, genre_id) values (?, ?)",
                    film.getId(), genre.getId());
        }
    }
}