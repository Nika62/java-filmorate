package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    public Mpa getMpaById(@PathVariable Optional<Integer> id) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        }
        return mpaService.getMpaById(id.get());
    }

    @DeleteMapping
    public boolean deleteMpa(@RequestBody Mpa mpa) {
        return mpaService.deleteMpa(mpa);
    }
}
