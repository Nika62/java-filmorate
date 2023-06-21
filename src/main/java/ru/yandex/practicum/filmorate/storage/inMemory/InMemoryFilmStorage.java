package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("filmInMemory")
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;
    private int filmId = 0;

    private long assignId(Film film) {
        film.setId(++filmId);
        return filmId;
    }

    @Override
    public Film addFilm(Film film) {
        if (films.containsValue(film)) {
            log.info("Произошла ошибка при добавлении фильма. {} уже существует.", film);
            throw new DataAlreadyExistsException("Фильм " + film + " уже существует.");
        }
        films.put(assignId(film), film);
        log.info("Добавлен новый фильм {}.", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм: " + film + " обновлен");
            return films.get(film.getId());
        } else {
            log.info("Произошла ошибка при обновлении фильма. {} отсутствует в фильмотеке.", film);
            throw new FilmNotFoundException("Фильм " + film + " отсутствует в фильмотеке.");
        }
    }

    @Override
    public boolean deleteFilm(long id) {
        if (!films.containsKey(id)) {
            log.info("Произошла ошибка при удалении фильма.{} отсутствует в фильмотеке.");
            throw new FilmNotFoundException("Фильм c id=" + id + " отсутствует в фильмотеке.");
        }
        films.remove(id);
        return true;
    }

    @Override
    public List<Film> getAllFilms() {
        if (films.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new ArrayList<Film>(films.values());
        }
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d отсутствует в фильмотеке.", id));
        }
        return films.get(id);
    }

    @Override
    public boolean addLikeFilm(long filmId, long userId) {
        checkFilm(filmId);
        checkUser(userId);
        Film film = films.get(filmId);
        return film.addLike(userId);
    }

    @Override
    public boolean deleteLikeFilm(long filmId, long userId) {
        checkFilm(filmId);
        checkUser(userId);
        Film film = films.get(filmId);
        return film.deleteLike(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new IncorrectPathVariableException("count");
        }
        List<Film> filmsList = (ArrayList<Film>) films.values().stream()
                .sorted(Film::compareTo);
        return filmsList.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new IncorrectPathVariableException("filmId");
        }
    }

    private void checkUser(long userId) {
        if (Objects.isNull(userStorage.getUserById(userId))) {
            throw new IncorrectPathVariableException("userId");
        }
    }
}
