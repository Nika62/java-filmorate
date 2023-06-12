package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.RequestDataBaseException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void shouldAddMpa() {
        Mpa mpaForAdd = new Mpa("ZD");
        Mpa returnedMpa = mpaDbStorage.addMpa(mpaForAdd);
        assertAll(
                () -> assertTrue(Objects.nonNull(returnedMpa.getId())),
                () -> assertEquals(returnedMpa.getName(), "ZD")
        );
    }

    @Test
    void shouldExceptionAddMpa() {
        Mpa mpaForAdd = new Mpa("G");
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    mpaDbStorage.addMpa(mpaForAdd);
                });

        assertEquals("Произошла ошибка при добавлении категории рейтинга " + mpaForAdd, e.getMessage());
    }

    @Test
    void shouldUpdateMpa() {
        Mpa mpaForUpdate = new Mpa(2, "GGG");
        Mpa returnedMpa = mpaDbStorage.updateMpa(mpaForUpdate);
        assertAll(
                () -> assertEquals(returnedMpa.getId(), 2),
                () -> assertEquals(returnedMpa.getName(), "GGG")
        );
    }

    @Test
    void shouldNotUpdateMpa() {
        Mpa mpaForUpdate = new Mpa(88);
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    mpaDbStorage.updateMpa(mpaForUpdate);
                });

        assertEquals("Произошла ошибка при обновлении категории рейтинга " + mpaForUpdate, e.getMessage());
    }

    @Test
    void shouldDeleteMpa() {
        Mpa mpaForDel = new Mpa(5);
        boolean resultDelete = mpaDbStorage.deleteMpa(mpaForDel);
        assertTrue(resultDelete);
    }

    @Test
    void shouldDeleteMpaIdNotDb() {
        Mpa mpaForDel = new Mpa(88);
        boolean resultDelete = mpaDbStorage.deleteMpa(mpaForDel);
        assertFalse(resultDelete);
    }

    @Test
    void shouldGetMpaById() {
        Mpa returnedMpa = mpaDbStorage.getMpaById(1);
        assertAll(
                () -> assertEquals(returnedMpa.getId(), 1),
                () -> assertEquals(returnedMpa.getName(), "G")
        );
    }

    @Test
    void shouldExceptionGetMpaById() {
        Exception e = Assertions.assertThrows(RequestDataBaseException.class,
                () -> {
                    mpaDbStorage.getMpaById(88);
                });

        assertEquals("Произошла ошибка при поиске категории рейтинга с id=88", e.getMessage());
    }

    @Test
    void shouldGetAllMpa() {
        List<Mpa> mpa = mpaDbStorage.getAllMpa();
        assertAll(
                () -> assertEquals(mpa.get(0).getName(), "G"),
                () -> assertEquals(mpa.get(0).getId(), 1),
                () -> assertEquals(mpa.get(3).getId(), 4),
                () -> assertEquals(mpa.get(3).getName(), "R"),
                () -> assertTrue(Objects.nonNull(mpa.get(1).getName()) && Objects.nonNull(mpa.get(2).getName()) && Objects.nonNull(mpa.get(3).getName()))
        );
    }
}