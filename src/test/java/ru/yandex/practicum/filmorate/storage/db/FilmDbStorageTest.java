package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserStorage userStorage;

    @Test
    void shouldAddFilm() {
        List<HashMap<String, Object>> genres = new ArrayList<>();
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        genres.add(mpa);
        Film testFilm = new Film("Убойные каникулы", "описание фильма убойные каникулы", LocalDate.parse("2001-01-01"), 98, mpa, genres);
        Film returnedFilm = filmDbStorage.addFilm(testFilm);
        assertAll(
                () -> assertEquals(returnedFilm.getId(), 1),
                () -> assertEquals(returnedFilm.getName(), "Убойные каникулы"),
                () -> assertEquals(returnedFilm.getDescription(), "описание фильма убойные каникулы"),
                () -> assertEquals(returnedFilm.getReleaseDate(), LocalDate.parse("2001-01-01")),
                () -> assertEquals(returnedFilm.getDuration(), 98),
                () -> assertEquals(returnedFilm.getMpa().get("id"), 1),
                () -> assertEquals(returnedFilm.getGenres().get(0).get("id"), 1)
        );
    }

    @Test
    void shouldExceptionAddFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        Film testFilm = new Film("Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa);
        filmDbStorage.addFilm(testFilm);
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.addFilm(testFilm);
                });
        assertEquals("Произошла ошибка при добавлении фильма " + testFilm, e.getMessage());
    }

    @Test
    void shouldUpdateFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        Film testFilm = new Film("Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa);
        filmDbStorage.addFilm(testFilm);
        Film forUpdateFilm = new Film(1L, "Приручить поросенка", "описание", LocalDate.parse("2008-08-08"), 177, mpa);
        Film afterUpdateFilm = filmDbStorage.updateFilm(forUpdateFilm);
        assertAll(
                () -> assertEquals(afterUpdateFilm.getId(), 1),
                () -> assertEquals(afterUpdateFilm.getName(), "Приручить поросенка"),
                () -> assertEquals(afterUpdateFilm.getDescription(), "описание"),
                () -> assertEquals(afterUpdateFilm.getReleaseDate(), LocalDate.parse("2008-08-08")),
                () -> assertEquals(afterUpdateFilm.getDuration(), 177),
                () -> assertEquals(afterUpdateFilm.getMpa().get("id"), 1)
        );
    }

    @Test
    void shouldExceptionUpdateFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 2);
        Film testFilm = new Film(40, "Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa);
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.updateFilm(testFilm);
                });
        assertEquals("Произошла ошибка при поиске фильма с id=40", e.getMessage());
    }

    @Test
    void shouldDeleteFilm() {
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        Film testFilm = new Film("Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa);
        filmDbStorage.addFilm(testFilm);
        assertTrue(filmDbStorage.deleteFilm(1));
    }

    @Test
    void shouldDeleteFilmIdNotInDb() {
        boolean resultDelete = filmDbStorage.deleteFilm(99);
        assertFalse(resultDelete);
    }

    @Test
    void shouldGetAllFilms() {
        List<HashMap<String, Object>> genres = new ArrayList<>();
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        genres.add(mpa);
        Film testFilm = new Film("Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa, genres);
        Film film = new Film("Приручить поросенка", "описание", LocalDate.parse("2008-08-08"), 177, mpa, genres);
        filmDbStorage.addFilm(testFilm);
        filmDbStorage.addFilm(film);
        List<Film> returnedListFilms = filmDbStorage.getAllFilms();
        assertAll(
                () -> assertEquals(returnedListFilms.size(), 2),
                () -> assertEquals(returnedListFilms.get(0).getName(), "Крадущийся тигр затаившийся дракон"),
                () -> assertEquals(returnedListFilms.get(0).getId(), 1),
                () -> assertEquals(returnedListFilms.get(1).getId(), 2),
                () -> assertEquals(returnedListFilms.get(1).getName(), "Приручить поросенка"),
                () -> assertEquals(returnedListFilms.get(0).getMpa().get("id"), 1),
                () -> assertTrue(Objects.nonNull(returnedListFilms.get(0).getGenres()))
        );
    }

    @Test
    void getFilmById1() {
        List<HashMap<String, Object>> genres = new ArrayList<>();
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        genres.add(mpa);
        Film testFilm = new Film("Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa, genres);
        filmDbStorage.addFilm(testFilm);
        Film returnedByIdFilm = filmDbStorage.getFilmById(1);
        assertAll(
                () -> assertEquals(returnedByIdFilm.getId(), 1),
                () -> assertEquals(returnedByIdFilm.getName(), "Крадущийся тигр затаившийся дракон"),
                () -> assertEquals(returnedByIdFilm.getDescription(), "описание Крадущийся тигр затаившийся дракон"),
                () -> assertEquals(returnedByIdFilm.getReleaseDate(), LocalDate.parse("2003-03-03")),
                () -> assertEquals(returnedByIdFilm.getDuration(), 120),
                () -> assertEquals(returnedByIdFilm.getMpa().get("id"), 1),
                () -> assertEquals(returnedByIdFilm.getMpa().get("name"), "G"),
                () -> assertEquals(returnedByIdFilm.getGenres().get(0).get("id"), 1),
                () -> assertEquals(returnedByIdFilm.getGenres().get(0).get("name"), "Комедия")
        );
    }

    @Test
    void shouldExceptionGetFilmById() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.getFilmById(40);
                });

        assertEquals("Произошла ошибка при поиске фильма с id=40", e.getMessage());
    }

    @Test
    void shouldAddLikeFilm() {
        List<HashMap<String, Object>> genres = new ArrayList<>();
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        genres.add(mpa);
        Film testFilm = new Film("Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa, genres);
        filmDbStorage.addFilm(testFilm);
        User user = new User("1","2", "3", LocalDate.parse("2020-02-02"));
        userStorage.addUser(user);
        assertTrue(filmDbStorage.addLikeFilm(1, 1));
    }

    @Test
    void shouldExceptionAddLikeFilm() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.addLikeFilm(55, 1);
                });
        assertEquals("Произошла ошибка при добавлении пользователем с id=1 лайка фильму с id=55", e.getMessage());
    }

    @Test
    void shouldDeleteLikeFilm() {
        List<HashMap<String, Object>> genres = new ArrayList<>();
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        genres.add(mpa);
        Film testFilm = new Film("Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa, genres);
        filmDbStorage.addFilm(testFilm);
        User user = new User("1","2", "3", LocalDate.parse("2020-02-02"));
        userStorage.addUser(user);
        filmDbStorage.addLikeFilm(1, 1);
        assertTrue(filmDbStorage.deleteLikeFilm(1, 1));
    }

    @Test
    void shouldExceptionDeleteLikeFilm() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    filmDbStorage.deleteLikeFilm(1, 67);
                });
        assertEquals("Произошла ошибка при удалении пользователем с id=67 лайка фильму с id=1", e.getMessage());
    }

    @Test
    void shouldGet9PopularFilms() {
        List<HashMap<String, Object>> genres = new ArrayList<>();
        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        genres.add(mpa);
        Film film = new Film("Крадущийся тигр затаившийся дракон", "описание Крадущийся тигр затаившийся дракон", LocalDate.parse("2003-03-03"), 120, mpa, genres);
        Film film2 = new Film("Приручить поросенка", "описание", LocalDate.parse("2008-08-08"), 177, mpa, genres);
        Film film3 = new Film("Фильм3", "описание Крадущийся тигр затаившийся дракон2", LocalDate.parse("2003-03-03"), 120, mpa, genres);
        Film film4 = new Film("Фильм4", "описание2", LocalDate.parse("2008-08-08"), 177, mpa, genres);
        filmDbStorage.addFilm(film);
        filmDbStorage.addFilm(film2);
        filmDbStorage.addFilm(film3);
        filmDbStorage.addFilm(film4);
        User user = new User("1","2", "3", LocalDate.parse("2020-02-02"));
        User user2 = new User("2","3", "4", LocalDate.parse("2020-02-02"));
        User user3 = new User("3","4", "5", LocalDate.parse("2020-02-02"));
        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        filmDbStorage.addLikeFilm(1, 1);
        filmDbStorage.addLikeFilm(2, 1);
        filmDbStorage.addLikeFilm(2, 2);
        filmDbStorage.addLikeFilm(2, 3);
        filmDbStorage.addLikeFilm(3, 1);
        filmDbStorage.addLikeFilm(3, 2);

        List<Film> listPopular = filmDbStorage.getPopularFilms(4);
        assertAll(
                () -> assertEquals(listPopular.size(), 4),
                () -> assertEquals(listPopular.get(0).getName(), "Приручить поросенка"),
                () -> assertEquals(listPopular.get(1).getName(), "Фильм3"),
                () -> assertEquals(listPopular.get(2).getName(), "Крадущийся тигр затаившийся дракон"),
                () -> assertEquals(listPopular.get(3).getName(), "Фильм4")
        );
    }
}