package ru.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.enums.Mpa;
import ru.filmorate.storage.db.MpaDbStorage;

import java.util.List;

/**
 * The type Mpa service
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    /**
     * Get all
     * @return the all mpas
     */

    public List<Mpa> getAll() {
        log.info("Получен список всех жанров");
        return mpaDbStorage.findAll();
    }

    /**
     * Get by id
     * @param id the id
     * @return the mpa
     */

    public Mpa getById(int id) {
        return mpaDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
    }
}