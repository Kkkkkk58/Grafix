package ru.itmo.grafix.core.exception;

public class ByteReaderException extends GrafixException {
    public ByteReaderException() {
        super("Failed to read a block of data from image");
    }
}
