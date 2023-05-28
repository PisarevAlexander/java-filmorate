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
}
