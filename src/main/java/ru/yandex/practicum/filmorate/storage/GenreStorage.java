package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre addGenre(Genre genre);

    Genre updateGenre(Genre genre);

    Boolean deleteGenre(Genre genre);

    Genre getGenreById(int id);

    List<Genre> getAllGenres();
}
