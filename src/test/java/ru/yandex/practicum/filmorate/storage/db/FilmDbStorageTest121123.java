package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest121123 {
    private final FilmDbStorage filmDbStorage;


    @Test
    void addFilm() {

        HashMap<String, Object> mpa = new HashMap<>();
        mpa.put("id", 1);
        Film film = new Film("Крадущийся тигр затаившийся дракон123123", "описание Крадущийся тигр затаившийся дракон123123", LocalDate.parse("2003-03-03"), 120, mpa);
        filmDbStorage.addFilm(film);
        filmDbStorage.addLikeFilm(1, 1);
        var actual = filmDbStorage.getAllFilms();
        // var actual1=filmDbStorage.getFilmById(1);
        System.out.println(actual);
        assertThat(actual).hasSize(1);
    }

}