package ru.itmo.grafix.core.exception;

public class InvalidGammaException extends GrafixException {
    public InvalidGammaException(String value) {
        super("Invalid gamma value: " + value);
    }
}
