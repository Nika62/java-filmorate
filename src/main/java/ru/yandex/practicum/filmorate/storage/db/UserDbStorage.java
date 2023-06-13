package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component("userDb")
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        checkUserName(user);

        String sqlInsert = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlInsert, new String[]{"id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);
            user.setId(keyHolder.getKey().longValue());
            log.info("Пользователь {} добавлен в базу", user);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при добавлении пользователя {}.", user);
            throw new RequestDataBaseException("Произошла ошибка при добавлении пользователя " + user);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlUpdate = "update users set id=?, email = ?, login= ?, name = ?, birthday = ? WHERE id =?;";
        try {
            boolean isUpdate = jdbcTemplate.update(sqlUpdate, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()) > 0;
            if (isUpdate) {
                log.info("Пользователь обновлен {}.", user);
                return user;
            } else {
                log.info("Произошла ошибка при обновлении пользователя {}.", user);
                throw new RequestDataBaseException("Произошла ошибка при обновлении пользователя " + user);
            }
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при обновлении пользователя {}.", user);
            throw new RequestDataBaseException("Произошла ошибка при обновлении пользователя " + user);
        }
    }

    @Override
    public boolean deleteUser(User user) {
        String sqlDelete = "DELETE FROM users WHERE id = ?;";
        return jdbcTemplate.update(sqlDelete, user.getId()) > 0;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM USERS;";
        try {
            return jdbcTemplate.query(sql, this::mapRowToUser);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при запросе всех пользователей");
            throw new RequestDataBaseException("Произошла ошибка при запросе всех пользователей");
        }
    }

    @Override
    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при поиске пользователя с id={}", id);
            throw new RequestDataBaseException("Произошла ошибка при поиске пользователя с id=" + id);
        }
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        String sqlAddFriend = "INSERT INTO FRIENDSHIP (USER_WHO_SEND_ID, USER_MUST_CONFIRM_ID, STATUS) VALUES(?, ?, ?);";
        try {
            return jdbcTemplate.update(sqlAddFriend, userId, friendId, "not_approved") > 0;
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при отправке заявки на дружбу пользователя с id={} к пользователю с id={}", userId, friendId);
            throw new RequestDataBaseException("Произошла ошибка при отправке заявки на дружбу пользователя с id=" + userId +
                    " к пользователю с id=" + friendId);
        }
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        String sqlDeleteFriend = "delete from friendship\n" +
                "where user_who_send_id in (?, ?) and user_must_confirm_id in(?, ?)";
        try {
            return jdbcTemplate.update(sqlDeleteFriend, userId, friendId, userId, friendId) > 0;

        } catch (DataAccessException e) {
            log.info("Произошла ошибка при удалении заявки на дружбу пользователя с id={} к пользователю с id={}", userId, friendId);
            throw new RequestDataBaseException("Произошла ошибка при удалении заявки на дружбу пользователя с id=" + userId +
                    " к пользователю с id=" + friendId);
        }
    }

    @Override
    public List<User> getListMutualFriends(long userId, long friendId) {
        String sql = "select *\n" +
                "from users u\n" +
                "where u.id in\n" +
                "      (select f1.user_must_confirm_id\n" +
                "       from friendship f1\n" +
                "       where f1.user_who_send_id in(?, ?) and f1.status = 'approved'\n" +
                "       group by f1.user_must_confirm_id\n" +
                "       having f1.user_must_confirm_id in (select f2.user_who_send_id\n" +
                "                                          from friendship f2\n" +
                "                                          where f2.user_must_confirm_id in(?, ?) and f2.status = 'approved'));";
        try {
            return jdbcTemplate.query(sql, this::mapRowToUser, userId, friendId);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при запросе общих друзей у пользователей с id={}, id={}", userId, friendId);
            throw new RequestDataBaseException("Произошла ошибка при запросе общих друзей у пользователей с id=" + userId + ", id=" + friendId);
        }
    }

    @Override
    public List<User> getListFriendsUser(long userId) {
        String sql = "SELECT u.ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY FROM USERS as u\n" +
                "WHERE u.ID IN (SELECT USER_MUST_CONFIRM_ID FROM FRIENDSHIP WHERE USER_WHO_SEND_ID =? and STATUS = 'approved')" +
                "or u.id in (SELECT USER_WHO_SEND_ID FROM FRIENDSHIP WHERE USER_MUST_CONFIRM_ID =? and STATUS = 'approved');";
        try {
            return jdbcTemplate.query(sql, this::mapRowToUser, userId, userId);
        } catch (DataAccessException e) {
            log.info("Произошла ошибка при запросе друзей пользователя с id={}", userId);
            throw new RequestDataBaseException("Произошла ошибка при запросе друзей пользователя с id=" + userId);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteAllUsers() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        String sql = "TRUNCATE TABLE users restart identity";
        jdbcTemplate.execute(sql);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteAllFriends() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        String sql = "TRUNCATE TABLE FRIENDSHIP restart identity";
        jdbcTemplate.execute(sql);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void checkUserName(User user) {
        if (Objects.isNull(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
