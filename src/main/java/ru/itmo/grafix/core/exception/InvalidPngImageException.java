package ru.itmo.grafix.core.exception;

public class InvalidPngImageException extends GrafixException {
    private InvalidPngImageException(String message) {
        super(message);
    }

    public static InvalidPngImageException incorrectChecksum() {
        return new InvalidPngImageException("Image contains corrupted chunks of data");
    }
}
