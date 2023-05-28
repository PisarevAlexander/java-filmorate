package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository("DBFilms")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query("SELECT f.film_id, f.name, f.description, f.release_date, " +
                        "f.duration, r.rating " +
                        "FROM films AS f " +
                        "LEFT JOIN mpa_rating AS r ON f.rating_id = r.rating_id",
                (rs, rowNum) -> makeFilm(rs));
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
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
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
        return film;
    }

    @Override
    public void update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return new Film(rs.getInt("film_id"), rs.getString("name"),
                rs.getString("description"), rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"), Mpa.valueOf(rs.getString("rating")));
    }
}