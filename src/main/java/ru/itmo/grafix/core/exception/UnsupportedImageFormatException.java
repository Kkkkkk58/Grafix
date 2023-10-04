package ru.itmo.grafix.core.exception;

public class UnsupportedImageFormatException extends GrafixException {
    public UnsupportedImageFormatException() {
        super("Unsupported image format");
    }
}
