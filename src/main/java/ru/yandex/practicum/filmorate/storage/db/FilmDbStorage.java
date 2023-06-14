package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDb")
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {

        String sqlInsert = "INSERT INTO films (title, description, release_date, duration, mpa) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlInsert, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, (Integer) film.getMpa().get("id"));
                return stmt;
            }, keyHolder);
            film.setId(keyHolder.getKey().longValue());
            log.info("Фильм {} добавлен в базу", film);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при добавлении фильма {}.", e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при добавлении фильма " + film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdate = "update films set id = ?, title = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE id =?;";
        Map<String, Object> mpa = film.getMpa();
        Integer mpaId = (Integer) mpa.get("id");
        if (Objects.nonNull(film.getGenres()) && film.getGenres().size() > 0) {
            updateGenreFilms(film);
            return film;
        }
        try {
            boolean isUpdate = jdbcTemplate.update(sqlUpdate, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaId, film.getId()) > 0;
            if (isUpdate) {
                log.info("Обновлен фильм {}.", film);
                return film;
            } else {
                log.info("Произошла ошибка при обновлении фильма {}.", film);
                throw new RequestDataBaseException("Произошла ошибка при обновлении фильма " + film);
            }
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при обновлении фильма {}", e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при обновлении фильма " + film);
        }
    }

    @Override
    public boolean deleteFilm(Film film) {
        String sqlDelete = "DELETE FROM films WHERE id = ?;";
        return jdbcTemplate.update(sqlDelete, film.getId()) > 0;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.ID,\n" +
                "                     f.TITLE,\n" +
                "                      f.DESCRIPTION,\n" +
                "                    f.RELEASE_DATE,\n" +
                "                      f.DURATION,\n" +
                "                      f.mpa,\n" +
                "                     r.TITLE,\n" +
                "                     g.ID,g.TITLE\n" +
                "FROM films AS f\n" +
                "JOIN RATING_MPA AS r ON f.MPA = r.id\n" +
                "JOIN FILMS_GENRES as fg ON fg.FILM_ID=f.ID\n" +
                "JOIN GENRES as g ON fg.GENRE_ID=g.ID\n" +
                " GROUP BY fg.GENRE_ID,g.id";
        try {
            List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
            return films;
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при запросе всех фильмов: {}", e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при запросе всех фильмов");
        }
    }

    @Override
    public Film getFilmById(long id) {
        String sql = "SELECT f.ID,\n" +
                "                     f.TITLE,\n" +
                "                      f.DESCRIPTION,\n" +
                "                    f.RELEASE_DATE,\n" +
                "                      f.DURATION,\n" +
                "                      f.mpa,\n" +
                "                     r.TITLE,\n" +
                "                     g.ID,g.TITLE\n" +
                "FROM films AS f\n" +
                "JOIN RATING_MPA AS r ON f.MPA = r.id\n" +
                "JOIN FILMS_GENRES as fg ON fg.FILM_ID=f.ID\n" +
                "JOIN GENRES as g ON fg.GENRE_ID=g.ID\n" +
                "WHERE f.id = ? GROUP BY fg.GENRE_ID,g.id";

        String sqlGenres = "SELECT GROUP_CONTACT(USER_ID) WHERE FILM_ID =?;";
        try {
            Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
            return film;
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при поиске фильма c id = {}, {}", id, e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при поиске фильма с id=" + id);
        }
    }
    //private void getGenres()

    public boolean addLikeFilm(long filmId, long userId) {
        String sqlAddLike = "INSERT INTO USERS_LIKE_FILMS (user_id, film_id) VALUES(?, ?);";
        try {
            log.info("Пользователь с id={} лайкнул фильм с id={}", userId, filmId);
            return jdbcTemplate.update(sqlAddLike, userId, filmId) > 0;
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при добавлении пользователем с id={} лайка фильму с id={}, {}", userId, filmId, e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при добавлении пользователем с id=" + userId + " лайка фильму с id=" + filmId);
        }
    }

    public boolean deleteLikeFilm(long filmId, long userId) {
        String sqlAddLike = "DELETE FROM USERS_LIKE_FILMS WHERE user_id=? AND film_id=?;";
        try {
            log.info("Пользователь с id={} удалил лайк  у фильма с id={}", userId, filmId);
            boolean isDel = jdbcTemplate.update(sqlAddLike, userId, filmId) > 0;
            if (!isDel) {
                log.info("Произошла ошибка при удалении пользователем с id={} лайка фильму с id={}", userId, filmId);
                throw new RequestDataBaseException("Произошла ошибка при удалении пользователем с id=" + userId + " лайка фильму с id=" + filmId);
            }
            return isDel;
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при удалении пользователем с id={} лайка фильму с id={}, {}", userId, filmId, e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при удалении пользователем с id=" + userId + " лайка фильму с id=" + filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        String sqlPopular = "SELECT F.*,\n" +
                "       RM.TITLE\n" +
                "FROM FILMS as F\n" +
                "LEFT JOIN USERS_LIKE_FILMS AS U ON F.ID = U.FILM_ID\n" +
                "JOIN RATING_MPA RM on RM.ID = F.MPA\n" +
                "GROUP BY F.ID\n" +
                "ORDER BY COUNT(USER_ID) DESC\n" +
                "LIMIT ?;";
        try {
            return jdbcTemplate.query(sqlPopular, this::mapRowToFilm, count);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при запросе популярных фильмов {}", e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при запросе популярных фильмов");
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        HashMap<String, Object> mpaMap = new HashMap<>();
        mpaMap.put("id", resultSet.getInt(6));
        mpaMap.put("name", resultSet.getString(7));
        List<HashMap<String, Object>> genres = new ArrayList<>();
        Film film = null;

        while (resultSet.next()) {
            if (film == null) {
                film = new Film((resultSet.getLong(1)), (resultSet.getString(2)), (resultSet.getString(3)),
                        (resultSet.getDate(4).toLocalDate()), (resultSet.getInt(5)), mpaMap, genres);
            }
            Genre genre = new Genre(resultSet.getInt(8), resultSet.getString(9));
            genres.add(genre.toMap());
        }
        if (film == null) {
            film = new Film((resultSet.getLong(1)), (resultSet.getString(2)), (resultSet.getString(3)),
                    (resultSet.getDate(4).toLocalDate()), (resultSet.getInt(5)), mpaMap, genres);
        }
        return film;
    }

    private List<HashMap<String, Object>> arrayToListHashMap(String[] array) {
        List<HashMap<String, Object>> genres = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", array[i]);
            genres.add(map);
        }
        return genres;
    }

    private void updateGenreFilms(Film film) {
        String sql = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES(?, ?);";
        List<Object> genresId = film.getGenres().stream()
                .map(m -> m.get("id"))
                .collect(Collectors.toList());
        try {
            for (int i = 0; i < genresId.size(); i++) {
                boolean isUpdate = jdbcTemplate.update(sql, film.getId(), (Integer) genresId.get(i)) > 0;
                if (!isUpdate) {
                    throw new RequestDataBaseException("Произошла ошибка при обновлении жанра фильма " + film);
                }
            }
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при обновлении жанра фильма {}", e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при обновлении жанра фильма " + film);
        }
    }
}
