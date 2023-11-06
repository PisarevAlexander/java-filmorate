package ru.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User controller test
 */

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    /**
     * Get user test
     */

    @Test
    public void getUserTest() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"));
    }

    /**
     * Post user test
     */

    @Test
    public void postUserTest() throws Exception {
        User user = new User(1, "mail@mail.ru", "dolore", "Nick Name",
                LocalDate.of(1946, 03, 25));

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"));
    }

    /**
     * Validation email test
     */

    @Test
    public void validationEmailTest() throws Exception {
        User user = new User(2, "mailmail.ru", "dolore", "Nick Name",
                LocalDate.of(1946, 03, 25));

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(mapper.writeValueAsString(user)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().string("{\"error\":\"Ошибка с полем \\\"email\\\".\"}"));
    }

    /**
     * Validation login test
     */

    @Test
    public void validationLoginTest() throws Exception {
        User user = new User(3, "mail@mail.ru", "", "Nick Name",
                LocalDate.of(1946, 03, 25));

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(mapper.writeValueAsString(user)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().string("{\"error\":\"Ошибка с полем \\\"login\\\".\"}"));
    }

    /**
     * Validation birthday test
     */

    @Test
    public void validationBirthdayTest() throws Exception {
        User user = new User(4, "mail@mail.ru", "login", "Nick Name",
                LocalDate.of(2100, 03, 25));

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(mapper.writeValueAsString(user)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().string("{\"error\":\"Ошибка с полем \\\"birthday\\\".\"}"));
    }

    /**
     * Validation update test
     */

    @Test
    public void validationUpdateTest() throws Exception {
        User user = new User(999, "mail@mail.ru", "dolore", "Nick Name",
                LocalDate.of(1946, 03, 25));

        this.mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpectAll(
                        status().is4xxClientError(),
                        result -> assertEquals("id " + user.getId() + " не найден",
                                result.getResolvedException().getMessage()));
    }
}