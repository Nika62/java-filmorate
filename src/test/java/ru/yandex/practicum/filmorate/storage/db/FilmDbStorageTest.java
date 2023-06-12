package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Order(1)
    @Test
    void shouldAddFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        Film testFilm = new Film("Убойные каникулы", "описание фильма убойные каникулы", LocalDate.parse("2001-01-01"), 98, mpa);
        Film returnedFilm = filmDbStorage.addFilm(testFilm);
        assertAll(
                () -> assertEquals(returnedFilm.getId(), 11),
                () -> assertEquals(returnedFilm.getName(), "Убойные каникулы"),
                () -> assertEquals(returnedFilm.getDescription(), "описание фильма убойные каникулы"),
                () -> assertEquals(returnedFilm.getReleaseDate(), LocalDate.parse("2001-01-01")),
                () -> assertEquals(returnedFilm.getDuration(), 98),
                () -> assertEquals(returnedFilm.getMpa().get("id"), 1)
        );
    }

    @Order(2)
    @Test
    void shouldExceptionAddFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 3);
        Film testFilm = new Film(1, "Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa);
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.addFilm(testFilm);
                });

        assertEquals("Произошла ошибка при добавлении фильма " + testFilm, e.getMessage());
    }

    @Order(3)
    @Test
    void shouldUpdateFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        Film forUpdateFilm = new Film(2l, "Приручить поросенка", "описание", LocalDate.parse("2008-08-08"), 177, mpa);
        Film afterUpdateFilm = filmDbStorage.updateFilm(forUpdateFilm);
        assertAll(
                () -> assertEquals(afterUpdateFilm.getId(), 2),
                () -> assertEquals(afterUpdateFilm.getName(), "Приручить поросенка"),
                () -> assertEquals(afterUpdateFilm.getDescription(), "описание"),
                () -> assertEquals(afterUpdateFilm.getReleaseDate(), LocalDate.parse("2008-08-08")),
                () -> assertEquals(afterUpdateFilm.getDuration(), 177),
                () -> assertEquals(afterUpdateFilm.getMpa().get("id"), 1)
        );
    }

    @Order(4)
    @Test
    void shouldExceptionUpdateFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 5);
        Film testFilm = new Film(40, "Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa);
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.updateFilm(testFilm);
                });

        assertEquals("Произошла ошибка при обновлении фильма " + testFilm, e.getMessage());
    }

    @Test
    @Order(5)
    void shouldDeleteFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 4);
        boolean resultDelete = filmDbStorage.deleteFilm(new Film(4, "Крадущийся тигр затаившийся дракон",
                "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa));
        assertTrue(resultDelete);

    }

    @Test
    @Order(6)
    void shouldDeleteFilmIdNotInDb() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 4);
        boolean resultDelete = filmDbStorage.deleteFilm(new Film(12, "тигр затаившийся дракон",
                "описание тигр затаившийся дракон", LocalDate.parse("2013-03-03"), 320, mpa));
        assertFalse(resultDelete);

    }

    @Test
    @Order(7)
    void shouldGetAllFilms() {
        List<Film> returnedListFilms = filmDbStorage.getAllFilms();
        assertAll(
                () -> assertEquals(returnedListFilms.size(), 9),
                () -> assertEquals(returnedListFilms.get(0).getName(), "Крадущийся тигр затаившийся дракон"),
                () -> assertEquals(returnedListFilms.get(0).getId(), 1),
                () -> assertEquals(returnedListFilms.get(8).getId(), 10),
                () -> assertEquals(returnedListFilms.get(8).getName(), "Мои маленькие пони"),
                () -> assertTrue(Objects.nonNull(returnedListFilms.get(0).getDuration()) && Objects.nonNull(returnedListFilms.get(0).getReleaseDate())
                        && Objects.nonNull(returnedListFilms.get(0).getMpa()))
        );
    }

    @Test
    @Order(8)
    void getFilmById1() {
        Film returnedByIdFilm = filmDbStorage.getFilmById(1);
        assertAll(
                () -> assertEquals(returnedByIdFilm.getId(), 1),
                () -> assertEquals(returnedByIdFilm.getName(), "Крадущийся тигр затаившийся дракон"),
                () -> assertEquals(returnedByIdFilm.getDescription(), "описание Крадущийся тигр затаившийся дракон"),
                () -> assertEquals(returnedByIdFilm.getReleaseDate(), LocalDate.parse("2003-03-03")),
                () -> assertEquals(returnedByIdFilm.getDuration(), 120),
                () -> assertEquals(returnedByIdFilm.getMpa().get("id"), 3)
        );
    }

    @Test
    @Order(9)
    void getFilmByMiddleId() {
        Film returnedByIdFilm = filmDbStorage.getFilmById(6l);
        assertAll(
                () -> assertEquals(returnedByIdFilm.getId(), 6),
                () -> assertEquals(returnedByIdFilm.getName(), "Звездные войны"),
                () -> assertEquals(returnedByIdFilm.getDescription(), "описание Звездные войны"),
                () -> assertEquals(returnedByIdFilm.getReleaseDate(), LocalDate.parse("2000-01-01")),
                () -> assertEquals(returnedByIdFilm.getDuration(), 210),
                () -> assertEquals(returnedByIdFilm.getMpa().get("id"), 3)
        );
    }

    @Test
    @Order(10)
    void shouldExceptionGetFilmById() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.getFilmById(40);
                });

        assertEquals("Произошла ошибка при поиске фильма с id=40", e.getMessage());
    }

    @Test
    @Order(11)
    void shouldAddLikeFilm() {
        assertTrue(filmDbStorage.addLikeFilm(3, 1));
    }

    @Test
    @Order(12)
    void shouldExceptionAddLikeFilm() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.addLikeFilm(55, 1);
                });
        assertEquals("Произошла ошибка при добавлении пользователем с id=1 лайка фильму с id=55", e.getMessage());
    }

    @Test
    @Order(13)
    void shouldDeleteLikeFilm() {
        assertTrue(filmDbStorage.deleteLikeFilm(1, 1));
    }

    @Test
    @Order(14)
    void shouldNotDeleteLikeFilm() {
        assertFalse(filmDbStorage.deleteLikeFilm(1, 67));
    }

    @Test
    @Order(15)
    void shouldGet9PopularFilms() {
        List<Film> listPopular = filmDbStorage.getPopularFilms(10);
        assertAll(
                () -> assertEquals(listPopular.size(), 7),
                () -> assertEquals(listPopular.get(0).getName(), "Смешарики"),
                () -> assertEquals(listPopular.get(3).getName(), "Приручить поросенка"),
                () -> assertEquals(listPopular.get(6).getName(), "Джанго")
        );
    }

    @Test
    @Order(16)
    void shouldGet3PopularFilms() {
        List<Film> listPopular = filmDbStorage.getPopularFilms(3);
        assertAll(
                () -> assertEquals(listPopular.size(), 3),
                () -> assertEquals(listPopular.get(0).getName(), "Смешарики"),
                () -> assertEquals(listPopular.get(1).getName(), "Карты деньги 2 ствола"),
                () -> assertEquals(listPopular.get(2).getName(), "Исчезнувшая")
        );
    }
}