package ru.yandex.practicum.filmorate.exceptions;

public class AddingDataException extends RuntimeException {
    public AddingDataException(final String massege) {
        super(massege);
    }
}
