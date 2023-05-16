package ru.yandex.practicum.filmorate.exception;

public class IncorrectPathVariableException extends RuntimeException {
    String variable;

    public IncorrectPathVariableException(String variable) {
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }
}
