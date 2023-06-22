package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    Mpa addMpa(Mpa mpa);

    Mpa updateMpa(Mpa mpa);

    boolean deleteMpa(int id);

    Mpa getMpaById(int id);

    List<Mpa> getAllMpa();
}
