package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.enums.FilmGenre;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void addGenre(int filmId, int genreId) {
        jdbcTemplate.update("INSERT INTO films_genre(film_id, genre_id) VALUES (?, ?)",
                filmId, genreId);
    }

    public void deleteGenresForFilm(int filmId) {
        jdbcTemplate.update("DELETE films_genre WHERE film_id = ?",
                filmId);
    }

    public List<FilmGenre> findAll() {
        List<FilmGenre> genres = jdbcTemplate.query("SELECT genre FROM genres", (rs, rowNum) ->
                FilmGenre.valueOf(rs.getString("genre")));
        return genres;
    }

    public Optional<FilmGenre> findById(int id) {
        SqlRowSet genresRow = jdbcTemplate.queryForRowSet("SELECT genre FROM genres WHERE genre_id = ?", id);
        if (genresRow.next()) {
            return Optional.of(FilmGenre.valueOf(genresRow.getString("genre")));
        } else {
            return Optional.empty();
        }
    }

    public List<Map<String, Object>> findGenreByFilms() {
        return jdbcTemplate.queryForList("SELECT f.film_id, g.genre " +
                "FROM films_genre AS f " +
                "JOIN genres AS g ON f.genre_id = g.genre_id");
    }

    public List<Map<String, Object>> findGenreByFilmsId(int id) {
        return jdbcTemplate.queryForList("SELECT g.genre FROM films_genre AS f " +
                "JOIN genres AS g ON f.genre_id = g.genre_id " +
                "WHERE film_id = ?", id);
    }

    public List<Map<String, Object>> findGenreForTopFilms(int count) {
        return jdbcTemplate.queryForList("SELECT f.film_id, g.genre " +
                "FROM films_genre AS f " +
                "JOIN genres AS g ON f.genre_id = g.genre_id " +
                "WHERE film_id IN (SELECT f.film_id " +
                "FROM films AS f " +
                "LEFT JOIN mpa_rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN users_liked_films AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY sum(l.user_id) DESC " +
                "LIMIT ?)", count);
    }
}
