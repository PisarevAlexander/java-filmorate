package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.enums.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public List<Mpa> getAll() {
        log.info("Получен список всех жанров");
        return mpaDbStorage.findAll();
    }

    public Mpa getById(int id) {
        return mpaDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("id " + id + " не найден"));
    }
}
