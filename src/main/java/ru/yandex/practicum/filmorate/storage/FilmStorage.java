package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteFilm(long id);

    List<Film> getAllFilms();

   Film getFilmById(long id);

    boolean addLikeFilm(long filmId, long userId);

    boolean deleteLikeFilm(long filmId, long userId);

    List<Film> getPopularFilms(int count);
}
