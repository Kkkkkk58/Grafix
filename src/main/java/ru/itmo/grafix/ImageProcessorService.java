package ru.itmo.grafix;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public interface ImageProcessorService {
    GrafixImage open(String absolutePath);
    ByteArrayOutputStream write(GrafixImage image);
}
