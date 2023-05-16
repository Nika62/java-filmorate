package ru.yandex.practicum.filmorate.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @GetMapping(produces = "application/json")
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Optional<Long> id) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        }
        return userStorage.getUserById(id.get());
    }

    @DeleteMapping
    public boolean deleteUser(@RequestBody User user) {
        return deleteUser(user);
    }
}
