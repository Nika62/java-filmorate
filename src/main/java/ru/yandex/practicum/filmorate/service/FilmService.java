package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFondException;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public boolean addLikeFilm(long filmId, long userId) {
        checkFilmById(filmId);
        checkUserById(userId);
        return filmStorage.getFilmById(filmId).addLike(userId);
    }

    public boolean deleteLikeFilm(long filmId, long userId) {
        checkUserById(userId);
        checkFilmById(filmId);
        return filmStorage.getFilmById(filmId).deleteLike(userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new IncorrectPathVariableException("count");
        }
        ArrayList<Film> films = (ArrayList<Film>) filmStorage.getAllFilms();
        Collections.sort(films, Film::compareTo);
        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkUserById(long userId) {
        if (Objects.isNull(userStorage.getUserById(userId))) {
            log.info("Пользователь с id: " + userId + " не зарегистрирован в базе.");
            throw new UserNotFoundException("Пользователь с id: " + userId + " не зарегистрирован в базе.");
        }
    }

    private void checkFilmById(long filmId) {
        if (Objects.isNull(filmStorage.getFilmById(filmId))) {
            log.info("Фильм с id: " + filmId + " не зарегистрирован в базе.");
            throw new FilmNotFondException("Фильм с id: " + filmId + " не зарегистрирован в базе.");
        }
    }
}
