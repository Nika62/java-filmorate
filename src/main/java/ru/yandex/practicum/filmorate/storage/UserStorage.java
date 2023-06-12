package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    boolean deleteUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    boolean addFriend(long userId, long friendId);

    boolean deleteFriend(long userId, long friendId);

    List<User> getListMutualFriends(long userId, long friendId);

    List<User> getListFriendsUser(long userId);
}
