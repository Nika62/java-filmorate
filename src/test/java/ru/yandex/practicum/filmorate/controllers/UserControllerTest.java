package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;


    private static Stream<Arguments> usersBadValidation() {
        return Stream.of(Arguments.of(new User("mail@mail.ru", "login", "матроскин", LocalDate.parse("3895-10-05")), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new User("mailmail.ru", "login", "матроскин", LocalDate.parse("1895-10-05")), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new User("mail@mail.ru", "   ", "матроскин", LocalDate.parse("1895-10-05")), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new User("mail@mail.ru", "login", "    ", LocalDate.parse("1895-10-05")), HttpStatus.OK.value()),
                Arguments.of(new User(), HttpStatus.BAD_REQUEST.value())

        );
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("usersBadValidation")
    void shouldValidationFailed(User user, int status) {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(status));
    }

    @SneakyThrows
    @Test
    void addUser() {
        User user = new User("mail@mail.ru", "login200", "матроскин200", LocalDate.parse("1895-10-05"));
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("login200"))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.name").value("матроскин200"))
                .andExpect(jsonPath("$.birthday").value("1895-10-05"))
        ;
    }

    private static Stream<Arguments> userUpdate() {
        return Stream.of(Arguments.of(new User(1, "mail@mail.ru", "login", "матроскин", LocalDate.parse("3895-10-05")), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new User(1, "mailmail.ru", "login", "матроскин", LocalDate.parse("1895-10-05")), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new User(1, "mail@mail.ru", "   ", "матроскин", LocalDate.parse("1895-10-05")), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new User(1, "mail@mail.ru", "login", "    ", LocalDate.parse("1895-10-05")), HttpStatus.OK.value()),
                Arguments.of(new User(), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new User(1, "mail@mail.ru", "login", "матроскин", LocalDate.parse("1895-10-05")), HttpStatus.OK.value())

        );

    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("userUpdate")
    void shouldUpdateUser(User user, int status) {
        User userBeforeUpdate = new User("mail@mail.ru", "Тpалл", "Джуфин", LocalDate.parse("1895-10-05"));
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(userBeforeUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(status));
    }

    @SneakyThrows
    @Test
    void shouldGetAllUsers() {
        User userBeforeUpdate = new User("mail@mail.ru", "Тpалл", "Джуфин", LocalDate.parse("1895-10-05"));
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(userBeforeUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                        get("/users")
                ).andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}