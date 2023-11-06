package ru.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.enums.FilmGenre;
import ru.filmorate.storage.db.GenreDbStorage;

import java.util.List;

/**
 * The type Genre service
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    /**
     * Get all
     * @return the all genres
     */

    public List<FilmGenre> getAll() {
        log.info("Получен список всех жанров");
        return genreDbStorage.findAll();
    }

    /**
     * Get by id
     * @param id the id
     * @return the genre
     */

    public FilmGenre getById(int id) {
        return genreDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
    }
}