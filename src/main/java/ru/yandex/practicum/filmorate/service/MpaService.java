package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa addMpa(Mpa mpa) {
        return mpaStorage.addMpa(mpa);
    }

    public Mpa updateMpa(Mpa mpa) {
        return mpaStorage.updateMpa(mpa);
    }

    public boolean deleteMpa(int id) {
        return mpaStorage.deleteMpa(id);
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}
