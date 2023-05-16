package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        checkFilm(film, filmId);
        checkUser(user, userId);
        return filmStorage.getFilmById(filmId).addLike(userId);
    }

    public boolean deleteLikeFilm(long filmId, long userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        checkFilm(film, filmId);
        checkUser(user, userId);
        return filmStorage.getFilmById(filmId).deleteLike(userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new IncorrectPathVariableException("count");
        }
        List<Film> films = filmStorage.getAllFilms();
        films.sort(Film::compareTo);

        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkUser(User user, long userId) {
        if (Objects.isNull(user)) {
            log.info("Пользователь с id {} не зарегистрирован в базе.", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован в базе.", userId));
        }
    }

    private void checkFilm(Film film, long filmId) {
        if (Objects.isNull(film)) {
            log.info("Фильм с id {} не зарегистрирован в базе.", filmId);
            throw new FilmNotFoundException(String.format("Фильм с id %d не зарегистрирован в базе.", filmId));
        }
    }
}
