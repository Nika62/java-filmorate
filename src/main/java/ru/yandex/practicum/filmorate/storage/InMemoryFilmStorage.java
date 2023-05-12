package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFondException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private int filmId = 0;

    private long assignId(Film film) {
        film.setId(++filmId);
        return filmId;
    }

    @Override
    public Film addFilm(Film film) {
        if (films.containsValue(film)) {
            log.info("Произошла ошибка при добавлении фильма. " + film + " уже существует.");
            throw new DataAlreadyExistsException("Фильм " + film + " уже существует.");

        }
        films.put(assignId(film), film);
        log.info("Добавлен новый фильм: " + film);
        return films.get(film.getId());

    }

    @Override
    public Film updateFilm(Film film) {
        System.out.println(films);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм: " + film + " обновлен");
            return films.get(film.getId());
        } else {
            log.info("Произошла ошибка при обновлении фильма. " + film + " отсутствует в фильмотеке.");
            throw new FilmNotFondException("Фильм " + film + " отсутствует в фильмотеке.");
        }
    }

    @Override
    public boolean deleteFilm(Film film) {
        if (!films.containsValue(film)) {
            log.info("Произошла ошибка при удалении фильма. " + film + " отсутствует в фильмотеке.");
            throw new FilmNotFondException("Фильм " + film + " отсутствует в фильмотеке.");
        }
        films.remove(film.getId());
        return true;
    }

    @Override
    public List<Film> getAllFilms() {
        return getFilms();
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFondException("Фильм с id " + id + " отсутствует в фильмотеке.");
        }
        return films.get(id);
    }

    private ArrayList<Film> getFilms() {
        if (films.isEmpty()) {
            return new ArrayList<>();
        } else {
            return (ArrayList<Film>) films.entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
    }
}
