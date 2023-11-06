package ru.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.filmorate.model.enums.FilmGenre;
import ru.filmorate.service.GenreService;

import java.util.List;

/**
 * Genre controller
 */

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    /**
     * Find by id film genre.
     * GET /genres/{id}
     * @param id the id
     * @return the film genre
     */

    @GetMapping("/genres/{id}")
    public FilmGenre findById(@PathVariable Integer id) {
        return genreService.getById(id);
    }

    /**
     * Find all list.
     * GET /genres
     * @return the list
     */

    @GetMapping("/genres")
    public List<FilmGenre> findAll() {
        return genreService.getAll();
    }
}