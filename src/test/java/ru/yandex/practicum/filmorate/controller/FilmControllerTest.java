package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

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
                25), 100L, Mpa.G);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"));
    }

    @Test
    public void validationDescriptionTest() throws Exception {
        Film film = new Film(2, "nisi eiusmod", "Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.",
                LocalDate.of(1967, 03, 25), 100L, Mpa.G);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(mapper.writeValueAsString(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().string("{\"error\":\"Ошибка с полем \\\"description\\\".\"}"));
    }

    @Test
    public void validationNameTest() throws Exception {
        Film film = new Film(3, "", "Пятеро друзей",
                LocalDate.of(1967, 03, 25), 100L, Mpa.G);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(mapper.writeValueAsString(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().string("{\"error\":\"Ошибка с полем \\\"name\\\".\"}"));
    }

    @Test
    public void validationDateTest() throws Exception {
        Film film = new Film(4, "name", "Пятеро друзей",
                LocalDate.of(1895, 12, 27), 100L, Mpa.G);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(mapper.writeValueAsString(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().string("{\"error\":\"Ошибка с полем \\\"releaseDate\\\".\"}"));
    }

    @Test
    public void validationDurationTest() throws Exception {
        Film film = new Film(5, "name", "Пятеро друзей",
                LocalDate.of(1895, 12, 28), 0L, Mpa.G);

        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(mapper.writeValueAsString(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().string("{\"error\":\"Ошибка с полем \\\"duration\\\".\"}"));
    }

    @Test
    public void validationUpdateTest() throws Exception {
        Film film = new Film(999, "name", "Пятеро друзей",
                LocalDate.of(1895, 12, 28), 100L, Mpa.G);

        this.mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film)))
                .andExpectAll(
                        status().is4xxClientError(),
                        result -> assertEquals("id " + film.getId() + " не найден",
                                result.getResolvedException().getMessage()));
    }
}