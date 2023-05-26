package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.enums.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> findAll() {
        List<Mpa> mpas = jdbcTemplate.query("select rating from mpa_rating", (rs, rowNum) ->
                Mpa.valueOf(rs.getString("rating")));
        return mpas;
    }

    public Optional<Mpa> findById(int id) {
        SqlRowSet mpasRow = jdbcTemplate.queryForRowSet("select rating from mpa_rating where rating_id = ?", id);
        if (mpasRow.next()) {
            return Optional.of(Mpa.valueOf(mpasRow.getString("rating")));
        } else {
            return Optional.empty();
        }
    }

}