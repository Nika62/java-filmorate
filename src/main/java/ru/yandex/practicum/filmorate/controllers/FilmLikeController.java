package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FilmLikeController {
    private final FilmService filmService;

    @PutMapping("films/{id}/like/{userId}")
    public boolean addLikeFilm(@PathVariable Optional<Long> id, Optional<Long> userId) {
        checkById(id, userId);
        return filmService.addLikeFilm(id.get(), userId.get());
    }

    @DeleteMapping("films/{id}/like/{userId}")
    public boolean deleteLikeFilm(@PathVariable Optional<Long> id, Optional<Long> userId) {
        checkById(id, userId);
        return filmService.deleteLikeFilm(id.get(), userId.get());
    }

    @PostMapping("films/popular?count={count}")
    public List<Film> getListBestByLikeFilms(@RequestParam(required = false) int count) {
        if (Objects.isNull(count)) {
            count = 10;
        }
        return filmService.getListBestByLikeFilms(count);
    }

    private void checkById(Optional<Long> id, Optional<Long> userId) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        } else if (!userId.isPresent()) {
            throw new IncorrectPathVariableException("userId");
        }
    }
}
