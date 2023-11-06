package ru.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.filmorate.model.enums.Mpa;

import java.util.List;
import java.util.Optional;

/**
 * The type Mpa db storage
 */

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Find all
     * @return the list of mpa
     */

    public List<Mpa> findAll() {
        List<Mpa> mpas = jdbcTemplate.query("SELECT rating FROM mpa_rating", (rs, rowNum) ->
                Mpa.valueOf(rs.getString("rating")));
        return mpas;
    }

    /**
     * Find by id
     * @param id the id
     * @return the optional of mpa
     */

    public Optional<Mpa> findById(int id) {
        SqlRowSet mpasRow = jdbcTemplate.queryForRowSet("SELECT rating FROM mpa_rating WHERE rating_id = ?", id);
        if (mpasRow.next()) {
            return Optional.of(Mpa.valueOf(mpasRow.getString("rating")));
        } else {
            return Optional.empty();
        }
    }
}