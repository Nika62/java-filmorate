package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    @Order(1)
    void shouldAddGenre() {
        Genre genreForAdd = new Genre("Фентези");
        Genre returnedGenre = genreDbStorage.addGenre(genreForAdd);
        assertAll(
                () -> assertEquals(returnedGenre.getId(), 7),
                () -> assertEquals(returnedGenre.getName(), "Фентези")
        );
    }

    @Test
    @Order(2)
    void shouldExceptionAddGenre() {
        Genre genreForAdd = new Genre("Комедия");
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    genreDbStorage.addGenre(genreForAdd);
                });

        assertEquals("Произошла ошибка при добавлении жанра " + genreForAdd, e.getMessage());
    }

    @Test
    @Order(3)
    void shouldUpdateGenre() {
        Genre genreForUpdate = new Genre(7, "Фантастика");
        Genre returnedGenre = genreDbStorage.updateGenre(genreForUpdate);
        assertAll(
                () -> assertEquals(returnedGenre.getId(), 7),
                () -> assertEquals(returnedGenre.getName(), "Фантастика")
        );
    }

    @Test
    @Order(4)
    void shouldExceptionUpdateGenre() {
        Genre genreForUpdate = new Genre(101);
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    genreDbStorage.updateGenre(genreForUpdate);
                });

        assertEquals("Произошла ошибка при поиске жанра с id=101", e.getMessage());
    }

    @Test
    @Order(5)
    void shouldDeleteGenre() {
        Genre genreForDel = new Genre(7);
        boolean resultDelete = genreDbStorage.deleteGenre(genreForDel.getId());
        assertTrue(resultDelete);
    }

    @Test
    @Order(6)
    void shouldDeleteGenreIdNotDb() {
        Genre genreForDel = new Genre(101);
        boolean resultDelete = genreDbStorage.deleteGenre(genreForDel.getId());
        assertFalse(resultDelete);
    }

    @Test
    @Order(7)
    void shouldGetGenreById() {
        Genre returnedGenre = genreDbStorage.getGenreById(1);
        assertAll(
                () -> assertEquals(returnedGenre.getId(), 1),
                () -> assertEquals(returnedGenre.getName(), "Комедия")
        );
    }

    @Test
    @Order(8)
    void shouldExceptionGetGenreById() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    genreDbStorage.getGenreById(101);
                });

        assertEquals("Произошла ошибка при поиске жанра с id=101", e.getMessage());
    }

    @Test
    @Order(9)
    void shouldGetAllGenres() {
        List<Genre> genres = genreDbStorage.getAllGenres();
        assertAll(
                () -> assertEquals(genres.size(), 6),
                () -> assertEquals(genres.get(0).getName(), "Комедия"),
                () -> assertEquals(genres.get(0).getId(), 1),
                () -> assertEquals(genres.get(5).getId(), 6),
                () -> assertEquals(genres.get(5).getName(), "Боевик"),
                () -> assertTrue(Objects.nonNull(genres.get(1).getName()) && Objects.nonNull(genres.get(2).getName())
                        && Objects.nonNull(genres.get(3).getName()) && Objects.nonNull(genres.get(4).getName()))
        );
    }
}