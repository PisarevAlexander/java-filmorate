package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.enums.FilmGenre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public List<FilmGenre> getAll() {
        log.info("Получен список всех жанров");
        return genreDbStorage.findAll();
    }

    public FilmGenre getById(int id) {
        return genreDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
    }
}
