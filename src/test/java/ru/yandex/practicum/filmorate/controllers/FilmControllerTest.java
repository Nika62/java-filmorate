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
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @SneakyThrows
    @Test
    void shouldGetAllFilm() {
        Film filmBeforeUpdate = new Film("Крадущийся тигр затаившийся лосось", "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        get("/films")
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].description").exists())
                .andExpect(jsonPath("$[*].releaseDate").exists())
                .andExpect(jsonPath("$[*].duration").exists());
    }

    @Test
    @SneakyThrows
    void shouldGetPopularFilms() {
        HashSet<Long> set1 = new HashSet<>(Arrays.asList(1l, 2l, 3l));
        HashSet<Long> set2 = new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l));
        HashSet<Long> set3 = new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l, 5l));
        HashSet<Long> set4 = new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l));
        HashSet<Long> set5 = new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l));
        HashSet<Long> set6 = new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l));
        HashSet<Long> set7 = new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l));
        HashSet<Long> set8 = new HashSet<>(Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l));
        HashSet<Long> set9 = new HashSet<>(Arrays.asList(1l, 2l));
        HashSet<Long> set10 = new HashSet<>(Arrays.asList(1l));

        Film filmBeforeUpdate1 = new Film("Крадущийся тигр затаившийся лосось1", set1, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate2 = new Film("Крадущийся тигр затаившийся лосось2", set2, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate3 = new Film("Крадущийся тигр затаившийся лосось3", set3, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate4 = new Film("Крадущийся тигр затаившийся лосось4", set4, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate5 = new Film("Крадущийся тигр затаившийся лосось5", set5, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate6 = new Film("Крадущийся тигр затаившийся лосось6", set6, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate7 = new Film("Крадущийся тигр затаившийся лосось7", set7, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate8 = new Film("Крадущийся тигр затаившийся лосось8", set8, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate9 = new Film("Крадущийся тигр затаившийся лосось9", set9, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        Film filmBeforeUpdate10 = new Film("Крадущийся тигр затаившийся лосось10", set10, "в кустах черники", LocalDate.of(1992, 12, 30), 90);
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate1))
                        .contentType(MediaType.APPLICATION_JSON)

        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate2))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate3))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate4))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate5))
                        .contentType(MediaType.APPLICATION_JSON)

        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate6))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate7))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate8))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate9))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(filmBeforeUpdate10))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                        get("/films/popular")
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Крадущийся тигр затаившийся лосось8"));

    }
}