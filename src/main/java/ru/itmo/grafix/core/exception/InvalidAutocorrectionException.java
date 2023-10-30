package ru.itmo.grafix.core.exception;

public class InvalidAutocorrectionException extends GrafixException {
    public InvalidAutocorrectionException(String value) {
        super("Invalid autocorrection parameter: " + value);
    }
}
