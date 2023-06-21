package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping
    public boolean deleteFilm(@PathVariable long id) {
        return filmService.deleteFilm(id);
    }

    @GetMapping(produces = "application/json")
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        if (id <= 0) {
            throw new IncorrectPathVariableException("id");
        }
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLikeFilm(@PathVariable long id, @PathVariable long userId) {
        if (id <= 0 || userId <= 0) {
            throw new IncorrectPathVariableException("id");
        }
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLikeFilm(@PathVariable long id, @PathVariable long userId) {
        if (id <= 0 || userId <= 0) {
            throw new IncorrectPathVariableException("id");
        }
        return filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

}
