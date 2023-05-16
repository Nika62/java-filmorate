package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
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
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;


    private static Stream<Arguments> filmsSave() {
        return Stream.of(
                Arguments.of(new Film("              ", "в кустах черники", LocalDate.parse("2020-10-05"), 30), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film("name", StringUtils.repeat('*', 205), LocalDate.parse("2020-10-05"), 30), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film("name", "в кустах черники", LocalDate.parse("1895-10-05"), 30), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film("name", "в кустах черники", LocalDate.parse("2020-10-05"), 0), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film(), HttpStatus.BAD_REQUEST.value())


        );

    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("filmsSave")
    void shouldValidationFailed(Film film, int status) {

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(status));
    }

    @Test
    @SneakyThrows
    void addFilm() {
        Film film = new Film("name2", "в кустах черники2", LocalDate.parse("2020-10-05"), 30);
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name2"))
                .andExpect(jsonPath("$.description").value("в кустах черники2"))
                .andExpect(jsonPath("$.releaseDate").value("2020-10-05"))
                .andExpect(jsonPath("$.duration").value("30"))
        ;
    }

    private static Stream<Arguments> filmsUpdate() {
        return Stream.of(
                Arguments.of(new Film(1, "              ", "в кустах черники", LocalDate.parse("2020-10-05"), 30), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film(1, "name", StringUtils.repeat('*', 205), LocalDate.parse("2020-10-05"), 30), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film(1, "name", "в кустах черники", LocalDate.parse("1895-10-05"), 30), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film(1, "name", "в кустах черники", LocalDate.parse("2020-10-05"), 0), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film(), HttpStatus.BAD_REQUEST.value()),
                Arguments.of(new Film(1, "name", "в кустах черники", LocalDate.parse("2020-10-05"), 30), HttpStatus.OK.value())

        );
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("filmsUpdate")
    void shouldUpdateFilm(Film film, int status) {
        Film filmBeforeUpdate = new Film("Крадущийся тигр затаившийся лосось", "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(status));
    }
}