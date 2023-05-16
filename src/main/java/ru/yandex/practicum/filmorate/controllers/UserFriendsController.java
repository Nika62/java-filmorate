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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("users/{id}/friends")
public class UserFriendsController {
    private final UserService userService;

    @PutMapping("/{friendId}")
    public boolean addFriend(@PathVariable Optional<Long> id, @PathVariable Optional<Long> friendId) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        } else if (!friendId.isPresent()) {
            throw new IncorrectPathVariableException("friendId");
        }

        return userService.addFriend(id.get(), friendId.get());
    }

    @DeleteMapping("/{friendId}")
    public boolean deleteFriend(@PathVariable Optional<Long> id, @PathVariable Optional<Long> friendId) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        } else if (!friendId.isPresent()) {
            throw new IncorrectPathVariableException("friendId");
        }
        return userService.deleteFriend(id.get(), friendId.get());
    }

    @GetMapping
    public List<User> getListFriendsUser(@PathVariable Optional<Long> id) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        }
        return userService.getListFriendsUser(id.get());
    }

    @GetMapping("/common/{otherId}")
    public List<User> getListMutualFriends(@PathVariable Optional<Long> id, @PathVariable Optional<Long> otherId) {
        if (!id.isPresent()) {
            throw new IncorrectPathVariableException("id");
        } else if (!otherId.isPresent()) {
            throw new IncorrectPathVariableException("otherId");
        }
        return userService.getListMutualFriends(id.get(), otherId.get());
    }
}
