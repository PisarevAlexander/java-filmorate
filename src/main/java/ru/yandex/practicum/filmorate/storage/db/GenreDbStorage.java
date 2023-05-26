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
        List<FilmGenre> genres = jdbcTemplate.query("select genre from genres", (rs, rowNum) ->
                FilmGenre.valueOf(rs.getString("genre")));
      return genres;
    }

    public Optional<FilmGenre> findById(int id) {
        SqlRowSet genresRow = jdbcTemplate.queryForRowSet("select genre from genres where genre_id = ?", id);
        if (genresRow.next()) {
            return Optional.of(FilmGenre.valueOf(genresRow.getString("genre")));
        } else {
            return Optional.empty();
        }
    }
}
