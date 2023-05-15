package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public boolean addFriend(long userId, long friendId) {
        checkUserById(userId);
        checkUserById(friendId);
        return userStorage.getUserById(userId).addFriend(friendId) && userStorage.getUserById(friendId).addFriend(userId);
    }

    public boolean deleteFriend(long userId, long friendId) {
        checkUserById(userId);
        checkUserById(friendId);

        return userStorage.getUserById(userId).deleteFriend(friendId) && userStorage.getUserById(friendId).deleteFriend(userId);
    }

    public List<User> getListMutualFriends(long userId, long friendId) {
        checkUserById(userId);
        checkUserById(friendId);
        return userStorage.getAllUsers().stream()
                .filter(u -> userStorage.getUserById(userId).getUserFriends().contains(u.getId())
                        && userStorage.getUserById(friendId).getUserFriends().contains(u.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getListFriendsUser(long userId) {
        checkUserById(userId);
        return userStorage.getAllUsers().stream()
                .filter(u -> userStorage.getUserById(userId).getUserFriends().contains(u.getId()))
                .collect(Collectors.toList());
    }

    private void checkUserById(long userId) {
        if (Objects.isNull(userStorage.getUserById(userId))) {
            log.info("Пользователь с id {} не зарегистрирован в базе.", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован в базе.", userId));
        }
    }

}
