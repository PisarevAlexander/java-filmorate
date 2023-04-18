package ru.yandex.practicum.filmorate.controller;

import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

     static class LocalDateAdapter implements JsonSerializer<LocalDate> {
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getFilmsTest() throws Exception {
        this.mockMvc.perform(get("/films"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"));
    }

    @Test
    public void postFilmsTest() throws Exception {
        Film film = new Film(1, "nisi eiusmod", "adipisicing", LocalDate.of(1967, 03,
                25), 100L);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        content().json(gson.toJson(film)));
    }

    @Test
    public void validationDescriptionTest() throws Exception {
        Film film = new Film(2, "nisi eiusmod", "Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                LocalDate.of(1967, 03, 25), 100L);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        result -> assertEquals("Описание не должно быть длиннее 200 символов",
                                result.getResolvedException().getMessage()));
    }

    @Test
    public void validationNameTest() throws Exception {
        Film film = new Film(3, "", "Пятеро друзей",
                LocalDate.of(1967, 03, 25), 100L);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        result -> assertEquals("Название не может быть пустым",
                                result.getResolvedException().getMessage()));
    }

    @Test
    public void validationDateTest() throws Exception {
        Film film = new Film(4, "name", "Пятеро друзей",
                LocalDate.of(1895, 12, 27), 100L);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        result -> assertEquals("Дата релиза не может быть раньше 1895-12-28",
                                result.getResolvedException().getMessage()));
    }

    @Test
    public void validationDurationTest() throws Exception {
        Film film = new Film(5, "name", "Пятеро друзей",
                LocalDate.of(1895, 12, 28), 0L);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        result -> assertEquals("Продолжительность фильма должна быть больше 0",
                                result.getResolvedException().getMessage()));
    }

    @Test
    public void updateTest() throws Exception {
        Film film1 = new Film(6, "name", "Пятеро друзей",
                LocalDate.of(1895, 12, 28), 100L);
        Film film2 = new Film(6, "name1", "Пятеро друзей1",
                LocalDate.of(1896, 12, 28), 100L);

        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(film1)));

        this.mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film2)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        content().json(gson.toJson(film2)));
    }

    @Test
    public void validationUpdateTest() throws Exception {
        Film film = new Film(999, "name", "Пятеро друзей",
                LocalDate.of(1895, 12, 28), 100L);

        this.mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        result -> assertEquals("id " + film.getId() + "не найден",
                                result.getResolvedException().getMessage()));
    }
}