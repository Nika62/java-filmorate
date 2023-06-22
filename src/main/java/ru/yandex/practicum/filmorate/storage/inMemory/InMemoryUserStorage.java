package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component("userInMemory")
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
            log.info("Произошла ошибка при добавлении пользователя. {} уже существует.", user);
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
            log.info("Произошла ошибка при обновлении пользователя. {} отсутствует в базе.", user);
            throw new UserNotFoundException("Пользователь " + user + " не зарегистрирован в базе.");
        }
    }

    @Override
    public boolean deleteUser(long id) {
        if (!users.containsKey(id)) {
            log.info("Произошла ошибка при удалении пользователя");
            throw new UserNotFoundException("Пользователь c id=" + id + " не зарегистрирован в базе.");
        }
        users.remove(id);
        return true;
    }

    @Override
    public List<User> getAllUsers() {
        if (users.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(users.values());
        }
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            log.info("Произошла ошибка при поиске пользователя по id. {} отсутствует в базе.", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован в базе.", id));
        }
        return users.get(id);
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        checkUserById(userId);
        checkUserById(friendId);
        return users.get(userId).addFriend(friendId) && users.get(friendId).addFriend(userId);
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        checkUserById(userId);
        checkUserById(friendId);
        return users.get(userId).deleteFriend(friendId) && users.get(friendId).deleteFriend(userId);
    }

    @Override
    public List<User> getListMutualFriends(long userId, long friendId) {
        checkUserById(userId);
        checkUserById(friendId);
        return users.values().stream()
                .filter(u -> users.get(userId).getUserFriends().contains(u.getId())
                        && users.get(friendId).getUserFriends().contains(u.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getListFriendsUser(long userId) {
        checkUserById(userId);
        if (Objects.nonNull(users)) {
            return users.values().stream()
                    .filter(u -> users.get(userId).getUserFriends().contains(u.getId()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private void checkUserById(long userId) {
        if (users.containsKey(userId)) {
            log.info("Пользователь с id {} не зарегистрирован в базе.", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован в базе.", userId));
        }
    }
}
