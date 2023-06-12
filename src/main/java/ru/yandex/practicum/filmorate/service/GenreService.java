package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre addGenre(Genre genre) {
        return genreStorage.addGenre(genre);
    }

    public Genre updateGenre(Genre genre) {
        return genreStorage.updateGenre(genre);
    }

    public boolean deleteGenre(Genre genre) {
        return genreStorage.deleteGenre(genre);
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

}
