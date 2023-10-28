package ru.itmo.grafix.core.exception;

public class InvalidSizeException extends GrafixException {
    public InvalidSizeException() {
        super("Size should be integer");
    }
}
