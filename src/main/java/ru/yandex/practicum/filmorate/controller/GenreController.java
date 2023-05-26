package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.enums.FilmGenre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres/{id}")
    public FilmGenre findById(@PathVariable Integer id) {
        return genreService.getById(id);
    }

    @GetMapping("/genres")
    public List<FilmGenre> findAll() {
        return genreService.getAll();
    }
}
