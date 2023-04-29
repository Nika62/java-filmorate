package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("films")
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    private int assignId(Film film) {
        film.setId(++filmId);
        return filmId;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (films.containsValue(film)) {
            log.info("Произошла ошибка при добавлении фильма. " + film + " уже существует");
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        }
        films.put(assignId(film), film);
        log.info("Добавлен новый фильм: " + film);
        return films.get(film.getId());

    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        System.out.println(films);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм: " + film + " обновлен");
            return films.get(film.getId());
        } else {
            log.info("Произошла ошибка при обновлении фильма. " + film + " отсутствует в фильмотеке");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(produces = "application/json")
    public List<Film> getAllFilms() {
        return getFilms();

    }

    private ArrayList<Film> getFilms() {
        if (films.isEmpty()) {
            return new ArrayList<>();
        } else {
            return (ArrayList<Film>) films.entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
    }
}
