package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre addGenre(Genre genre) {
        String sqlInsert = "INSERT INTO GENRES (title) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlInsert, new String[]{"id"});
                stmt.setString(1, genre.getName());
                return stmt;
            }, keyHolder);
            genre.setId(keyHolder.getKey().intValue());
            log.info("Жанр {} добавлен в базу", genre);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при добавлении жанра {}.", genre);
            throw new RequestDataBaseException("Произошла ошибка при добавлении жанра " + genre);
        }
        return genre;
    }

    @Override
    public Genre updateGenre(Genre genre) {
        String sqlUpdate = "UPDATE GENRES SET title=? WHERE id=?;";
        try {
            boolean isUpdate = jdbcTemplate.update(sqlUpdate, genre.getName(), genre.getId()) > 0;
            if (isUpdate) {
                log.info("Обновлен жанр {}", genre);
                return genre;
            } else {
                log.info("Произошла ошибка при обновлении жанра {}.", genre);
                throw new RequestDataBaseException("Произошла ошибка при обновлении жанра " + genre);
            }
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при обновлении жанра {}.", genre);
            throw new RequestDataBaseException("Произошла ошибка при обновлении жанра " + genre);
        }
    }

    @Override
    public Boolean deleteGenre(Genre genre) {

        String sqlDelete = "DELETE FROM GENRES WHERE id=?;";
        return jdbcTemplate.update(sqlDelete, genre.getId()) > 0;
    }

    @Override
    public Genre getGenreById(int id) {
        String sql = "SELECT * FROM GENRES  WHERE ID=?;";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToGenres, id);
        } catch (Exception e) {
            log.info("Произошла ошибка при поиске жанра с id={}", id);
            throw new RequestDataBaseException("Произошла ошибка при поиске жанра с id=" + id);
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRES ORDER BY ID;";
        try {
            return jdbcTemplate.query(sql, this::mapRowToGenres);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при запросе всех жанров");
            throw new RequestDataBaseException("Произошла ошибка при запросе всех жанров");
        }
    }

    private Genre mapRowToGenres(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt(1),
                rs.getString(2)
        );
    }
}
