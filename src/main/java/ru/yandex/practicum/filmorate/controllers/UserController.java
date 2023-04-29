package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 0;

    private int assignId(User user) {
        user.setId(++userId);
        return userId;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (users.containsValue(user)) {
            log.info("Произошла ошибка при добавлении пользователя. " + user + " уже существует");
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(assignId(user), user);
        log.info("Добавлен новый пользователь: " + user);
        return users.get(user.getId());


    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь: " + user + " обновлен");
            return users.get(user.getId());
        } else {
            log.info("Произошла ошибка при обновлении пользователя. " + user + " отсутствует в базе");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(produces = "application/json")
    public List<User> getAllUsers() {
        return getUsers();
    }

    private ArrayList<User> getUsers() {
        if (users.isEmpty()) {
            return new ArrayList<>();
        } else {
            return (ArrayList<User>) users.entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
    }
}
