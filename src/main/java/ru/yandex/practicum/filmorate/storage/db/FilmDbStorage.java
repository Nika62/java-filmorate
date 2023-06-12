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
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            log.info("Произошла ошибка при добавлении фильма {}.", film);
            throw new RequestDataBaseException("Произошла ошибка при добавлении фильма " + film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdate = "update films set id = ?, title = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE id =?;";
        Map<String, Object> mpa = film.getMpa();
        Integer mpaId = (Integer) mpa.get("id");
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
            log.info("Произошла ошибка при обновлении фильма {}.", film);
            e.getMessage();
            e.getStackTrace();
            throw new RuntimeException(e);
//             throw new RequestDataBaseException("Произошла ошибка при обновлении фильма " +film);
        }
    }

    @Override
    public boolean deleteFilm(Film film) {
        String sqlDelete = "DELETE FROM films WHERE id = ?;";
        return jdbcTemplate.update(sqlDelete, film.getId()) > 0;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.id, f.title, f.description, f.release_date, f.duration, f.mpa as mpa_id, r.title AS mpa_title, group_concat(FG.GENRE_ID)\n" +
                "FROM  FILMS  as f JOIN RATING_MPA as r ON f.MPA = r.ID JOIN FILMS_GENRES AS FG ON f.ID = FG.FILM_ID\n" +
                "group by f.id;";
        try {
            return jdbcTemplate.query(sql, this::mapRowToFilm);
        } catch (DataAccessException e) {
            log.info("Произщшла ошибка при запросе всех фильмов");
            throw new RequestDataBaseException("Произщшла ошибка при запросе всех фильмов");
        }
    }

    @Override
    public Film getFilmById(long id) {
        String sql = "SELECT f.ID,\n" +
                "       f.TITLE,\n" +
                "       f.DESCRIPTION,\n" +
                "       f.RELEASE_DATE,\n" +
                "       f.DURATION,\n" +
                "       r.ID,\n" +
                "       r.TITLE,\n" +
                "       group_concat(FG.GENRE_ID)\n" +
                "FROM films AS f JOIN RATING_MPA AS r ON f.MPA = r.id JOIN FILMS_GENRES AS FG ON f.ID = FG.FILM_ID\n" +
                "WHERE f.id = ?\n" +
                "GROUP BY f.ID;";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при поиске фильма с id={}", id);
            throw new RequestDataBaseException("Произошла ошибка при поиске фильма с id=" + id);
        }
    }

    public boolean addLikeFilm(long filmId, long userId) {
        String sqlAddLike = "INSERT INTO USERS_LIKE_FILMS (user_id, film_id) VALUES(?, ?);";
        try {
            log.info("Пользователь с id={} лайкнул фильм с id={}", userId, filmId);
            return jdbcTemplate.update(sqlAddLike, userId, filmId) > 0;
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при добавлении пользователем с id={} лайка фильму с id={}", userId, filmId);
            throw new RequestDataBaseException("Произошла ошибка при добавлении пользователем с id=" + userId + " лайка фильму с id=" + filmId);
        }
    }

    public boolean deleteLikeFilm(long filmId, long userId) {
        String sqlAddLike = "DELETE FROM USERS_LIKE_FILMS WHERE user_id=? AND film_id=?;";
        try {
            log.info("Пользователь с id={} удалил лайк  у фильма с id={}", userId, filmId);
            return jdbcTemplate.update(sqlAddLike, userId, filmId) > 0;
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при удалении пользователем с id={} лайка фильму с id={}", userId, filmId);
            throw new RequestDataBaseException("Произошла ошибка при удалении пользователем с id=" + userId + " лайка фильму с id=" + filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        String sqlPopular = "SELECT f.id,\n" +
                "       f.title,\n" +
                "       f.description,\n" +
                "       f.release_date,\n" +
                "       f.duration,\n" +
                "       f.mpa,\n" +
                "       r.title,\n" +
                "       group_concat(FG.GENRE_ID)\n" +
                "FROM FILMS AS f JOIN RATING_MPA as r ON f.MPA = r.ID JOIN FILMS_GENRES AS FG ON f.ID = FG.FILM_ID\n" +
                "WHERE f.id IN(SELECT ulf.film_id\n" +
                "From USERS_LIKE_FILMS as ulf\n" +
                "GROUP BY ulf.film_id\n" +
                "ORDER BY count(ulf.user_id) desc)\n" +
                "GROUP BY f.ID;";
        try {
            return jdbcTemplate.query(sqlPopular, this::mapRowToFilm).stream().limit(count).collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.info("Произoшла ошибка при запросе популярных фильмов");
            throw new RequestDataBaseException("Произошла ошибка при запросе популярных фильмов");
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        HashMap<String, Object> mpaMap = new HashMap<>();
        mpaMap.put("id", resultSet.getInt(6));
        mpaMap.put("name", resultSet.getString(7));
        return Film.builder()
                .id(resultSet.getLong(1))
                .name(resultSet.getString(2))
                .description(resultSet.getString(3))
                .releaseDate(resultSet.getDate(4).toLocalDate())
                .duration(resultSet.getInt(5))
                .mpa(mpaMap)
                .genres(stringConvertList(resultSet.getString(8)))
                .build();
    }

    private List<Integer> stringConvertList(String str) {
        String[] array = str.split(",");
        return Arrays.stream(array)
                .map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());
    }
}
