package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @DeleteMapping
    public boolean deleteFilm(@RequestBody Film film) {
        return filmStorage.deleteFilm(film);
    }

    @GetMapping(produces = "application/json")
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Optional<Long> id) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        }
        return filmStorage.getFilmById(id.get());
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLikeFilm(@PathVariable Optional<Long> id, @PathVariable Optional<Long> userId) {
        checkById(id, userId);
        return filmService.addLikeFilm(id.get(), userId.get());
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLikeFilm(@PathVariable Optional<Long> id, @PathVariable Optional<Long> userId) {
        checkById(id, userId);
        return filmService.deleteLikeFilm(id.get(), userId.get());
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    private void checkById(Optional<Long> id, Optional<Long> userId) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        } else if (!userId.isPresent()) {
            throw new IncorrectPathVariableException("userId");
        }
    }
}
