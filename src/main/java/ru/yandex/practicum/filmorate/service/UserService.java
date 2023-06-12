package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDb")
    private final UserStorage userStorage;

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public boolean delete(User user) {
        return userStorage.deleteUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public boolean addFriend(long userId, long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public boolean deleteFriend(long userId, long friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getListMutualFriends(long userId, long friendId) {
        return userStorage.getListMutualFriends(userId, friendId);
    }

    public List<User> getListFriendsUser(long userId) {
        return userStorage.getListFriendsUser(userId);
    }


}
