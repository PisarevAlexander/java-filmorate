package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.FilmGenre;
import ru.yandex.practicum.filmorate.model.enums.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository("DBFilms")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("SELECT f.film_id, f.name, f.description, f.release_date, " +
                        "f.duration, r.rating " +
                        "FROM films AS f " +
                        "LEFT JOIN mpa_rating AS r ON f.rating_id = r.rating_id",
                (rs, rowNum) -> makeFilm(rs));
        List<Map<String, Object>> genres = jdbcTemplate.queryForList("SELECT f.film_id, g.genre " +
                "FROM films_genre AS f " +
                "JOIN genres AS g ON f.genre_id = g.genre_id");
        List<Map<String, Object>> likedUsers = jdbcTemplate.queryForList("SELECT * FROM users_liked_films");
        Map<Integer, Film> mapFilms = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        for (Map<String, Object> genre : genres) {
            mapFilms.get(Integer.parseInt(genre.get("FILM_ID").toString())).getGenres()
                    .add(FilmGenre.valueOf(genre.get("GENRE").toString()));
        }
        for (Map<String, Object> likedUser : likedUsers) {
            mapFilms.get(Integer.parseInt(likedUser.get("FILM_ID").toString())).getUserLikeFilm()
                    .add(Integer.parseInt(likedUser.get("USER_ID").toString()));
        }
        return films;
    }

    @Override
    public Optional<Film> findById(int filmId) {
        try {
            Film film = jdbcTemplate.queryForObject("SELECT f.film_id, f.name, f.description, f.release_date, " +
                            "f.duration, r.rating " +
                            "FROM films AS f " +
                            "LEFT JOIN mpa_rating AS r ON f.rating_id = r.rating_id " +
                            "WHERE film_id = ?",
                    ((rs, rowNum) -> makeFilm(rs)), filmId);
            SqlRowSet likedUsers = jdbcTemplate.queryForRowSet("SELECT user_id " +
                    "FROM users_liked_films " +
                    "WHERE film_id = ?", film.getId());
            while (likedUsers.next()) {
                film.addUserId(likedUsers.getInt("user_id"));
            }
            SqlRowSet genres = jdbcTemplate.queryForRowSet("SELECT g.genre FROM films_genre AS f " +
                            "JOIN genres AS g ON f.genre_id = g.genre_id " +
                            "WHERE film_id = ?",
                    film.getId());
            while (genres.next()) {
                film.addGenre(FilmGenre.valueOf(genres.getString("genre")));
            }
            return Optional.of(film);
        } catch (DataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findTop(int count) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, r.rating " +
                "FROM films AS f " +
                "LEFT JOIN mpa_rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN users_liked_films AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY sum(l.user_id) DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
        for (Film film : films) {
            SqlRowSet likedUsers = jdbcTemplate.queryForRowSet("SELECT user_id " +
                    "FROM users_liked_films " +
                    "WHERE film_id = ?", film.getId());
            while (likedUsers.next()) {
                film.addUserId(likedUsers.getInt("user_id"));
            }
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
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
    public void update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (!film.getUserLikeFilm().isEmpty()) {
            jdbcTemplate.update("DELETE users_liked_films WHERE film_id = ?", film.getId());
            Set<Integer> likedUsers = film.getUserLikeFilm();
            for (Integer likedUser : likedUsers) {
                jdbcTemplate.update("INSERT INTO users_liked_films(film_id, user_id) VALUES (?, ?)",
                        film.getId(), likedUser);
            }
        }
        jdbcTemplate.update("DELETE films_genre WHERE film_id = ?", film.getId());
        if (!film.getGenres().isEmpty()) {
            setGenres(film);
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return new Film(rs.getInt("film_id"), rs.getString("name"),
                rs.getString("description"), rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"), Mpa.valueOf(rs.getString("rating")));
    }

    private void setGenres(Film film) {
        Set<FilmGenre> genres = film.getGenres();
        for (FilmGenre genre : genres) {
            jdbcTemplate.update("INSERT INTO films_genre(film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.getId());
        }
    }
}