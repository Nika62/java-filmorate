package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @PostMapping
    public Mpa addMpa(@Valid @RequestBody Mpa mpa) {
        return mpaService.addMpa(mpa);
    }

    @PutMapping
    public Mpa updateMpa(@Valid @RequestBody Mpa mpa) {
        return mpaService.updateMpa(mpa);
    }

    @GetMapping(produces = "application/json")
    public List<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        if (id<=0) {
            throw new IncorrectPathVariableException("id");
        }
        return mpaService.getMpaById(id);
    }

    @DeleteMapping
    public boolean deleteMpa(@PathVariable int id) {
        return mpaService.deleteMpa(id);
    }
}
