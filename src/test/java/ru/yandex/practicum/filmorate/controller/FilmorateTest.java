package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FilmGenre;
import ru.yandex.practicum.filmorate.model.enums.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmorateTest {

    @Qualifier("DBUsers")
    private final UserStorage userStorage;

    @Qualifier("DBFilms")
    private final FilmStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    private static User firstUser;
    private static User secondUser;
    private static Film firstFilm;
    private static Film secondFilm;

    @BeforeEach
    public void beforeEach() {
        firstUser = new User(0, "email1@email.ru", "login1", "name1",
                LocalDate.of(1993, 02, 12));
        secondUser = new User(0, "email2@email.ru", "login2", "name2",
                LocalDate.of(1990, 10, 5));
        firstFilm = new Film(0, "name1", "description1",
                LocalDate.of(2000, 1, 1), 100L, Mpa.G);
        secondFilm = new Film(0, "name2", "description2",
                LocalDate.of(2010, 2, 2), 100L, Mpa.NC17);
    }

    @Test
    public void getGenreTest() {
        assertEquals(genreStorage.findAll(), List.of(FilmGenre.values()));
    }

    @Test
    public void getGenreByIdTest() {
        assertEquals(genreStorage.findById(1).get(), FilmGenre.COMEDY);
    }

    @Test
    public void getMpaTest() {
        assertEquals(mpaStorage.findAll(), List.of(Mpa.values()));
    }

    @Test
    public void getMpaByIdTest() {
        assertEquals(mpaStorage.findById(1).get(), Mpa.G);
    }

    @Test
    public void addFilmTest() {
        Film film = filmStorage.create(firstFilm);

        assertEquals(film.getId(), 1);
        assertEquals(film.getName(), firstFilm.getName());
        assertEquals(film.getDescription(), firstFilm.getDescription());
        assertEquals(film.getReleaseDate(), firstFilm.getReleaseDate());
        assertEquals(film.getMpa(), firstFilm.getMpa());
    }

    @Test
    public void getFilmTest() {
        filmStorage.create(firstFilm);
        Film film = filmStorage.findById(firstFilm.getId()).get();

        assertEquals(firstFilm.getId(), film.getId());
        assertEquals(firstFilm.getName(), film.getName());
        assertEquals(firstFilm.getDescription(), film.getDescription());
        assertEquals(firstFilm.getReleaseDate(), film.getReleaseDate());
        assertEquals(firstFilm.getMpa(), film.getMpa());
    }

    @Test
    public void updateFilmTest() {
        filmStorage.create(firstFilm);
        firstFilm.setDescription("updated");
        filmStorage.update(firstFilm);
        Film film = filmStorage.findById(firstFilm.getId()).get();

        assertEquals(firstFilm.getId(), film.getId());
        assertEquals(firstFilm.getName(), film.getName());
        assertEquals(firstFilm.getDescription(), film.getDescription());
        assertEquals(firstFilm.getReleaseDate(), film.getReleaseDate());
        assertEquals(firstFilm.getMpa(), film.getMpa());
    }

    @Test
    public void addUserTest() {
        User user = userStorage.create(firstUser);

        assertEquals(user.getId(), 1);
        assertEquals(user.getName(), firstUser.getName());
        assertEquals(user.getLogin(), firstUser.getLogin());
        assertEquals(user.getBirthday(), firstUser.getBirthday());
    }

    @Test
    public void getUserTest() {
        userStorage.create(firstUser);
        User user = userStorage.findById(firstUser.getId()).get();

        assertEquals(user.getId(), firstUser.getId());
        assertEquals(user.getName(), firstUser.getName());
        assertEquals(user.getLogin(), firstUser.getLogin());
        assertEquals(user.getBirthday(), firstUser.getBirthday());
    }

    @Test
    public void updateUserTest() {
        userStorage.create(firstUser);
        firstUser.setName("updated");
        userStorage.update(firstUser);
        User user = userStorage.findById(firstUser.getId()).get();

        assertEquals(user.getId(), firstUser.getId());
        assertEquals(user.getName(), firstUser.getName());
        assertEquals(user.getLogin(), firstUser.getLogin());
        assertEquals(user.getBirthday(), firstUser.getBirthday());
    }

    @Test
    public void getAllFilmTest() {
        filmStorage.create(firstFilm);
        filmStorage.create(secondFilm);

        assertEquals(filmStorage.findAll(), List.of(firstFilm, secondFilm));
    }

    @Test
    public void getAllUserTest() {
        userStorage.create(firstUser);
        userStorage.create(secondUser);

        assertEquals(userStorage.findAll(), List.of(firstUser, secondUser));
    }
}
