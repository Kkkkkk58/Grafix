package ru.itmo.grafix;

import ru.itmo.grafix.api.ColorSpace;

import java.io.ByteArrayOutputStream;

public interface ImageProcessorService {
    GrafixImage open(String absolutePath, ColorSpace colorSpace);
    ByteArrayOutputStream write(GrafixImage image);
}
