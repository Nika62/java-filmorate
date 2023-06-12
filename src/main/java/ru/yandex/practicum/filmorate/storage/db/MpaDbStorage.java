package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa addMpa(Mpa mpa) {
        String sqlInsert = "INSERT INTO RATING_MPA (title) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlInsert, new String[]{"id"});
                stmt.setString(1, mpa.getName());
                return stmt;
            }, keyHolder);
            mpa.setId(keyHolder.getKey().intValue());
            log.info("Новая категория рейтинга {} добавлена в базу", mpa);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при добавлении категории рейтинга {}, {}.", mpa, e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при добавлении категории рейтинга " + mpa);
        }
        return mpa;
    }

    @Override
    public Mpa updateMpa(Mpa mpa) {
        String sqlUpdate = "UPDATE RATING_MPA SET title=? WHERE id=?;";
        try {
            boolean isUpdate = jdbcTemplate.update(sqlUpdate, mpa.getName(), mpa.getId()) > 0;
            if (isUpdate) {
                log.info("Обновлена категория рейтинга {}", mpa);
                return mpa;
            } else {
                log.info("Произошла ошибка при обновлении катигории рейтинга {}.", mpa);
                throw new RequestDataBaseException("Произошла ошибка при обновлении категории рейтинга " + mpa);
            }
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при обновлении катигории рейтинга {}, {}", mpa, e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при обновлении категории рейтинга " + mpa);
        }
    }

    @Override
    public boolean deleteMpa(Mpa mpa) {
        String sqlDelete = "DELETE FROM RATING_MPA WHERE id=?;";
        return jdbcTemplate.update(sqlDelete, mpa.getId()) > 0;
    }

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM RATING_MPA WHERE ID=?;";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
        } catch (Exception e) {
            log.info("Произошла ошибка при поиске категории рейтинга с id={}, {}", id, e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при поиске категории рейтинга с id=" + id);
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM RATING_MPA GROUP BY ID;";
        try {
            return jdbcTemplate.query(sql, this::mapRowToMpa);
        } catch (Exception e) {
            log.info("Произошла ошибка при запросе всех категорий рейтинга {}", e.getMessage());
            throw new RequestDataBaseException("Произошла ошибка при запросе всех категорий рейтинга");
        }
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNun) throws SQLException {
        return new Mpa(rs.getInt(1),
                rs.getString(2)
        );
    }
}
