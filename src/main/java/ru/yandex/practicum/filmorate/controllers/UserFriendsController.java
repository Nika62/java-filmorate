package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.IncorrectPathVariableException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("users/{id}/friends")
public class UserFriendsController {
    private final UserService userService;

    @PutMapping("/{friendId}")
    public boolean addFriend(@PathVariable long id, @PathVariable long friendId) {
        if (id <= 0) {
            throw new IncorrectPathVariableException("id");
        } else if (friendId <= 0) {
            throw new IncorrectPathVariableException("friendId");
        }
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{friendId}")
    public boolean deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        if (id <= 0) {
            throw new IncorrectPathVariableException("id");
        } else if (friendId <= 0) {
            throw new IncorrectPathVariableException("friendId");
        }
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping
    public List<User> getListFriendsUser(@PathVariable long id) {
        if (id <= 0) {
            throw new IncorrectPathVariableException("id");
        }
        return userService.getListFriendsUser(id);
    }

    @GetMapping("/common/{otherId}")
    public List<User> getListMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        if (id <= 0) {
            throw new IncorrectPathVariableException("id");
        } else if (otherId <= 0) {
            throw new IncorrectPathVariableException("otherId");
        }
        return userService.getListMutualFriends(id, otherId);
    }
}
