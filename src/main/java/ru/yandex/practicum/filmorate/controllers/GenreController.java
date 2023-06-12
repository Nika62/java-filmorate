package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @PostMapping
    public Genre addGenre(@Valid @RequestBody Genre genre) {
        return genreService.addGenre(genre);
    }

    @PutMapping
    public Genre updateGenre(@Valid @RequestBody Genre genre) {
        return genreService.updateGenre(genre);
    }

    @GetMapping(produces = "application/json")
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Optional<Integer> id) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        }
        return genreService.getGenreById(id.get());
    }
    @DeleteMapping
    public boolean deleteGenre(@RequestBody Genre genre) {
        return genreService.deleteGenre(genre);
    }
}
