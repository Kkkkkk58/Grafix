package ru.itmo.grafix.core.exception;

public class ByteWriterException extends GrafixException {
    public ByteWriterException() {
        super("Failed to write image data to file");
    }
}
