package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String massege) {
        super(massege);
    }
}
