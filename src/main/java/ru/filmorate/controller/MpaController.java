package ru.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.filmorate.model.enums.Mpa;
import ru.filmorate.service.MpaService;

import java.util.List;

/**
 * Mpa controller
 */

@RestController
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    /**
     * Find by id mpa.
     * GET /mpa/{id}
     * @param id the id
     * @return the mpa
     */

    @GetMapping("/mpa/{id}")
    public Mpa findById(@PathVariable Integer id) {
        return mpaService.getById(id);
    }

    /**
     * Find all list.
     * GET /mpa
     * @return the list of mpa
     */

    @GetMapping("/mpa")
    public List<Mpa> findAll() {
        return mpaService.getAll();
    }
}