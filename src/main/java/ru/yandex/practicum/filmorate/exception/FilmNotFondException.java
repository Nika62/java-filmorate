package ru.yandex.practicum.filmorate.exception;

public class FilmNotFondException extends RuntimeException {
    public FilmNotFondException(String massege) {
        super(massege);
    }
}
