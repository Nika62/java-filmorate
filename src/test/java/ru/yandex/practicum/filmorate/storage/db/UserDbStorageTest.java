package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));

    @BeforeEach
    public void before() {
        userDbStorage.addUser(userForAddDb);
    }

    @AfterEach
    public void after() {
        userDbStorage.deleteAllUsers();
    }

    @Test
    void shouldGetUserById() {
        User user = userDbStorage.getUserById(1l);
        assertAll(
                () -> assertEquals(user.getId(), 1),
                () -> assertEquals(user.getEmail(), "user@mail.com"),
                () -> assertEquals(user.getLogin(), "log"),
                () -> assertEquals(user.getName(), "name"),
                () -> assertEquals(user.getBirthday(), LocalDate.parse("2010-12-13"))
        );
    }

    @Test
    void shouldAddUser() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        User returnedUser = userDbStorage.addUser(userForAddDb);
        assertAll(
                () -> assertEquals(returnedUser.getId(), 1),
                () -> assertEquals(returnedUser.getEmail(), "user@mail.com"),
                () -> assertEquals(returnedUser.getLogin(), "log"),
                () -> assertEquals(returnedUser.getName(), "name"),
                () -> assertEquals(returnedUser.getBirthday(), LocalDate.parse("2010-12-13"))
        );
    }

    @Test
    void shouldExceptionAddUser() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("1990-02-01"));
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    userDbStorage.addUser(userForAddDb);
                });

        assertEquals("Произошла ошибка при добавлении пользователя " + userForAddDb, e.getMessage());
    }

    @Test
    void updateUser() {
        User userForUpdate = new User(1l, "user@mailUpdate.com", "logUpdate", "nameUpdate", LocalDate.parse("2010-12-13"));
        userDbStorage.updateUser(userForUpdate);
        User returnedUser = userDbStorage.getUserById(1l);
        assertAll(
                () -> assertEquals(returnedUser.getId(), 1),
                () -> assertEquals(returnedUser.getEmail(), "user@mailUpdate.com"),
                () -> assertEquals(returnedUser.getLogin(), "logUpdate"),
                () -> assertEquals(returnedUser.getName(), "nameUpdate"),
                () -> assertEquals(returnedUser.getBirthday(), LocalDate.parse("2010-12-13"))
        );
    }

    @Test
    void shouldExceptionUpdateUser() {
        User userForUpdate = new User(23, "mail@mail2.ru", "login", "name", LocalDate.parse("1990-02-01"));
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    userDbStorage.updateUser(userForUpdate);
                });

        assertEquals("Произошла ошибка при обновлении пользователя " + userForUpdate, e.getMessage());
    }

    @Test
    void shouldDeleteUser() {
        boolean resultDelete = userDbStorage.deleteUser(new User(4, "mail@mail2.ru", "login", "name", LocalDate.parse("1990-02-01")));
        assertTrue(resultDelete);

    }

    @Test
    void shouldDeleteUserIdNotInDb() {
        boolean resultDelete = userDbStorage.deleteUser(new User(101, "mail@mail2.ru", "login", "name", LocalDate.parse("1990-02-01")));
        assertFalse(resultDelete);
    }

    @Test
    void shouldGetAllUsers() {
        List<User> returnedListUsers = userDbStorage.getAllUsers();
        System.out.println(returnedListUsers);
        assertAll(
                () -> assertEquals(returnedListUsers.get(0).getName(), "name"),
                () -> assertEquals(returnedListUsers.get(0).getId(), 1),
                () -> assertEquals(returnedListUsers.size(), 1)
        );
    }

    @Test
    void shouldGetUserById1() {
        User returnedByIdUser = userDbStorage.getUserById(1l);
        assertAll(
                () -> assertEquals(returnedByIdUser.getId(), 1),
                () -> assertEquals(returnedByIdUser.getEmail(), "mail@mail2.ru"),
                () -> assertEquals(returnedByIdUser.getLogin(), "login"),
                () -> assertEquals(returnedByIdUser.getName(), "name"),
                () -> assertEquals(returnedByIdUser.getBirthday(), LocalDate.parse("1990-02-01"))
        );
    }

    @Test
    void shouldGetUserByMiddleId() {
        User returnedByIdUser = userDbStorage.getUserById(3l);
        assertAll(
                () -> assertEquals(returnedByIdUser.getId(), 3),
                () -> assertEquals(returnedByIdUser.getEmail(), "mail@mail4.ru"),
                () -> assertEquals(returnedByIdUser.getLogin(), "Дарт Вейдер"),
                () -> assertEquals(returnedByIdUser.getName(), "Энакин"),
                () -> assertEquals(returnedByIdUser.getBirthday(), LocalDate.parse("1992-07-11"))
        );
    }

    @Test
    void shouldExceptionGetUserById() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    userDbStorage.getUserById(23l);
                });

        assertEquals("Произошла ошибка при поиске пользователя с id=23", e.getMessage());
    }

    @Test
    void shouldAddFriend() {
        boolean result = userDbStorage.addFriend(1, 3);
        assertTrue(result);
    }

    @Test
    void shouldExceptionAddFriend() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    userDbStorage.addFriend(1, 2);
                });

        assertEquals("Произошла ошибка при отправке заявки на дружбу пользователя с id=1 к пользователю с id=2", e.getMessage());
    }

    @Test
    void shouldDeleteFriend() {
        boolean result = userDbStorage.deleteFriend(1, 2);
        assertTrue(result);
    }

    @Test
    void shouldNotDeleteFriend() {
        boolean result = userDbStorage.deleteFriend(1, 4);
        assertFalse(result);
    }

    @Test
    void shouldGetListMutualFriends() {

    }

    @Test
    void shouldGetListFriendsUser() {
        List<User> friends = userDbStorage.getListFriendsUser(2);
        assertAll(
                () -> assertEquals(friends.size(), 2),
                () -> assertEquals(friends.get(0).getId(), 1),
                () -> assertEquals(friends.get(1).getId(), 6)
        );
    }
}