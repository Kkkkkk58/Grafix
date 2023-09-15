package ru.itmo.grafix.exception;

public class UnsupportedImageFormatException extends GrafixException {
    public UnsupportedImageFormatException() {
        super("Unsupported image format");
    }
}
