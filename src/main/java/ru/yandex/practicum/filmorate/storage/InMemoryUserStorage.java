package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private long userId = 0;

    private long assignId(User user) {
        user.setId(++userId);
        return userId;
    }

    @Override
    public User addUser(User user) {
        if (users.containsValue(user)) {
            log.info("Произошла ошибка при добавлении пользователя. " + user + " уже существует.");
            throw new DataAlreadyExistsException("Пользователь " + user + " уже существует.");
        }
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(assignId(user), user);
        log.info("Добавлен новый пользователь " + user);
        return user;


    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь: " + user + " обновлен");
            return users.get(user.getId());
        } else {
            log.info("Произошла ошибка при обновлении пользователя. " + user + " отсутствует в базе.");
            throw new UserNotFoundException("Пользователь " + user + " не зарегистрирован в базе.");
        }
    }

    @Override
    public boolean deleteUser(User user) {
        if (!users.containsValue(user)) {
            log.info("Произошла ошибка при удалении пользователя. " + user + " отсутствует в базе.");
            throw new UserNotFoundException("Пользователь " + user + " не зарегистрирован в базе.");
        }
        users.remove(user.getId());
        return true;
    }

    @Override
    public List<User> getAllUsers() {
        return getUsers();
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            log.info("Произошла ошибка при поиске пользователя по id. Id " + id + " отсутствует в базе.");
            throw new UserNotFoundException("Пользователь с id " + id + " не зарегистрирован в базе.");
        }
        return users.get(id);
    }

    private ArrayList<User> getUsers() {
        if (users.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(users.values());
        }
    }
}
