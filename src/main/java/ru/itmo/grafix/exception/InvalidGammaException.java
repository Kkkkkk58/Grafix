package ru.itmo.grafix.exception;

public class InvalidGammaException extends GrafixException{
    public InvalidGammaException(String value) {
        super("Invalid gamma value: " + value);
    }
}
