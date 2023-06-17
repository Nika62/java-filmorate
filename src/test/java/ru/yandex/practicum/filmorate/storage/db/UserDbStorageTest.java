package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

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
        User userForAddDb = new User("mail@mail2.ru", "login", "name", LocalDate.parse("1990-02-01"));
        userDbStorage.addUser(userForAddDb);
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    userDbStorage.addUser(userForAddDb);
                });

        assertEquals("Произошла ошибка при добавлении пользователя " + userForAddDb, e.getMessage());
    }

    @Test
    void updateUser() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        User userForUpdate = new User(1L, "userUp@mail.com", "logUp", "nameUp", LocalDate.parse("2010-12-13"));
        User returnedUser = userDbStorage.updateUser(userForUpdate);
        assertAll(
                () -> assertEquals(returnedUser.getId(), 1),
                () -> assertEquals(returnedUser.getEmail(), "userUp@mail.com"),
                () -> assertEquals(returnedUser.getLogin(), "logUp"),
                () -> assertEquals(returnedUser.getName(), "nameUp"),
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
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        boolean resultDelete = userDbStorage.deleteUser(new User(1L, "mail@mail2.ru", "login", "name", LocalDate.parse("1990-02-01")));
        assertTrue(resultDelete);

    }

    @Test
    void shouldDeleteUserIdNotInDb() {
        boolean resultDelete = userDbStorage.deleteUser(new User(101, "mail@mail2.ru", "login", "name", LocalDate.parse("1990-02-01")));
        assertFalse(resultDelete);
    }

    @Test
    void shouldGetAllUsers() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        User userForAddDb2 = new User("user@mail2.com", "log2", "name2", LocalDate.parse("2010-12-13"));
        User userForAddDb3 = new User("user@mail3.com", "log3", "name3", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        userDbStorage.addUser(userForAddDb2);
        userDbStorage.addUser(userForAddDb3);
        List<User> returnedListUsers = userDbStorage.getAllUsers();
        System.out.println(returnedListUsers);
        assertAll(
                () -> assertEquals(returnedListUsers.get(0).getName(), "name"),
                () -> assertEquals(returnedListUsers.get(0).getId(), 1),
                () -> assertEquals(returnedListUsers.get(1).getId(), 2),
                () -> assertEquals(returnedListUsers.get(2).getId(), 3),
                () -> assertEquals(returnedListUsers.get(0).getName(), "name"),
                () -> assertTrue(Objects.nonNull(returnedListUsers.get(0).getEmail()) && Objects.nonNull(returnedListUsers.get(0).getLogin())
                        && Objects.nonNull(returnedListUsers.get(0).getBirthday()))
        );
    }

    @Test
    void shouldGetUserById1() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        User returnedByIdUser = userDbStorage.getUserById(1L);
        assertAll(
                () -> assertEquals(returnedByIdUser.getId(), 1),
                () -> assertEquals(returnedByIdUser.getEmail(), "user@mail.com"),
                () -> assertEquals(returnedByIdUser.getLogin(), "log"),
                () -> assertEquals(returnedByIdUser.getName(), "name"),
                () -> assertEquals(returnedByIdUser.getBirthday(), LocalDate.parse("2010-12-13"))
        );
    }

    @Test
    void shouldGetUserByMiddleId() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        User userForAddDb2 = new User("user@mail2.com", "log2", "name2", LocalDate.parse("2010-12-13"));
        User userForAddDb3 = new User("user@mail3.com", "log3", "name3", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        userDbStorage.addUser(userForAddDb2);
        userDbStorage.addUser(userForAddDb3);
        User returnedByIdUser = userDbStorage.getUserById(2L);
        assertAll(
                () -> assertEquals(returnedByIdUser.getId(), 2),
                () -> assertEquals(returnedByIdUser.getEmail(), "user@mail2.com"),
                () -> assertEquals(returnedByIdUser.getLogin(), "log2"),
                () -> assertEquals(returnedByIdUser.getName(), "name2"),
                () -> assertEquals(returnedByIdUser.getBirthday(), LocalDate.parse("2010-12-13"))
        );
    }

    @Test
    void shouldExceptionGetUserById() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    userDbStorage.getUserById(23L);
                });

        assertEquals("Произошла ошибка при поиске пользователя с id=23", e.getMessage());
    }

    @Test
    void shouldAddFriend() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        User userForAddDb2 = new User("user@mail2.com", "log2", "name2", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        userDbStorage.addUser(userForAddDb2);
        boolean result = userDbStorage.addFriend(1, 2);
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
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        User userForAddDb2 = new User("user@mail2.com", "log2", "name2", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        userDbStorage.addUser(userForAddDb2);
        userDbStorage.addFriend(1, 2);
        boolean result = userDbStorage.deleteFriend(1, 2);
        assertTrue(result);
    }

    @Test
    void shouldNotDeleteFriend() {
        boolean result = userDbStorage.deleteFriend(1, 3);
        assertFalse(result);
    }

    @Test
    void shouldGetListMutualFriends() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        User userForAddDb2 = new User("user@mail2.com", "log2", "name2", LocalDate.parse("2010-12-13"));
        User userForAddDb3 = new User("user@mail3.com", "log3", "name3", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        userDbStorage.addUser(userForAddDb2);
        userDbStorage.addUser(userForAddDb3);
        userDbStorage.addFriend(1, 2);
        userDbStorage.addFriend(3, 2);
        List<User> friends = userDbStorage.getListMutualFriends(1L, 3L);
        assertAll(
                () -> assertEquals(friends.size(), 1),
                () -> assertEquals(friends.get(0).getId(), 2)
        );
    }

    @Test
    void shouldGetListFriendsUser() {
        User userForAddDb = new User("user@mail.com", "log", "name", LocalDate.parse("2010-12-13"));
        User userForAddDb2 = new User("user@mail2.com", "log2", "name2", LocalDate.parse("2010-12-13"));
        userDbStorage.addUser(userForAddDb);
        userDbStorage.addUser(userForAddDb2);
        userDbStorage.addFriend(1L, 2L);
        List<User> friends = userDbStorage.getListFriendsUser(1L);
        assertAll(
                () -> assertEquals(friends.size(), 1),
                () -> assertEquals(friends.get(0).getId(), 2)
        );
    }
}